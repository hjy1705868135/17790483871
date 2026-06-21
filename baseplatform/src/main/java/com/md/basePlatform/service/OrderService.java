package com.md.basePlatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.exception.OrderException;
import com.md.basePlatform.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                       OrderAddressRepository orderAddressRepository,
                       OrderStatusHistoryRepository orderStatusHistoryRepository,
                       ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderAddressRepository = orderAddressRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * 创建订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailResponse createOrder(CreateOrderRequest request, Long userId) {
        String orderNo = generateOrderNo();

        BigDecimal totalAmount = calculateTotalAmount(request.getItems());

        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .totalAmount(totalAmount)
                .status("PENDING_PAYMENT")
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("UNPAID")
                .remark(request.getRemark())
                .build();

        orderRepository.insert(order);

        // 检查库存并扣减
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                    Product product = productRepository.selectById(item.getProductId());
                    if (product == null) {
                        throw new OrderException("商品不存在: " + item.getProductId());
                    }

                    // 检查库存
                    if (product.getStock() < item.getQuantity()) {
                        throw new OrderException("商品库存不足: " + product.getName() + "，剩余库存: " + product.getStock());
                    }

                    // 扣减库存
                    product.setStock(product.getStock() - item.getQuantity());
                    productRepository.updateById(product);

                    return OrderItem.builder()
                            .orderId(order.getId())
                            .productId(item.getProductId())
                            .productName(product.getName())
                            .productImage(product.getImage())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .subtotal(subtotal)
                            .build();
                })
                .collect(Collectors.toList());

        orderItems.forEach(orderItemRepository::insert);

        OrderAddress orderAddress = OrderAddress.builder()
                .orderId(order.getId())
                .receiverName(request.getShippingAddress().getReceiverName())
                .phone(request.getShippingAddress().getPhone())
                .province(request.getShippingAddress().getProvince())
                .city(request.getShippingAddress().getCity())
                .district(request.getShippingAddress().getDistrict())
                .detailAddress(request.getShippingAddress().getDetailAddress())
                .postalCode(request.getShippingAddress().getPostalCode())
                .build();

        orderAddressRepository.insert(orderAddress);

        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .orderId(order.getId())
                .status("PENDING_PAYMENT")
                .remark("订单创建")
                .operator("SYSTEM")
                .build();

        orderStatusHistoryRepository.insert(statusHistory);

        log.info("Order created successfully: orderNo={}, userId={}", orderNo, userId);

        return getOrderDetail(order.getId());
    }

    /**
     * 获取订单详情
     */
    public OrderDetailResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new OrderException("订单不存在");
        }

        List<OrderItem> orderItems = orderItemRepository.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
        );

        OrderAddress orderAddress = orderAddressRepository.selectOne(
                new LambdaQueryWrapper<OrderAddress>()
                        .eq(OrderAddress::getOrderId, orderId)
        );

        List<OrderStatusHistory> statusHistoryList = orderStatusHistoryRepository.selectList(
                new LambdaQueryWrapper<OrderStatusHistory>()
                        .eq(OrderStatusHistory::getOrderId, orderId)
                        .orderByAsc(OrderStatusHistory::getCreatedAt)
        );

        return buildOrderDetailResponse(order, orderItems, orderAddress, statusHistoryList);
    }

    /**
     * 分页查询订单列表
     */
    public IPage<OrderSummaryResponse> getOrders(Long userId, String status, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        IPage<Order> orderPage = orderRepository.selectUserOrders(pageParam, userId, status);

        return orderPage.convert(order -> OrderSummaryResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .itemCount(getOrderItemCount(order.getId()))
                .createdAt(order.getCreatedAt())
                .build());
    }

    /**
     * 更新订单状态
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, String operator) {
        Order order = orderRepository.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new OrderException("订单不存在");
        }

        if (!isValidStatusTransition(order.getStatus(), request.getStatus())) {
            throw new OrderException("无效的状态转换");
        }

        order.setStatus(request.getStatus());
        orderRepository.updateById(order);

        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .orderId(orderId)
                .status(request.getStatus())
                .remark(request.getRemark())
                .operator(operator)
                .build();

        orderStatusHistoryRepository.insert(statusHistory);

        log.info("Order status updated: orderId={}, status={}, operator={}", orderId, request.getStatus(), operator);

        return getOrderDetail(orderId);
    }

    /**
     * 删除订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new OrderException("订单不存在");
        }

        if ("COMPLETED".equals(order.getStatus())) {
            throw new OrderException("已完成订单不能删除");
        }

        // 逻辑删除
        order.setDeleted(1);
        orderRepository.updateById(order);

        log.info("Order deleted: orderId={}", orderId);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ORD" + timestamp + uuid;
    }

    private BigDecimal calculateTotalAmount(List<CreateOrderRequest.OrderItemRequest> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Integer getOrderItemCount(Long orderId) {
        return Math.toIntExact(orderItemRepository.selectCount(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
        ));
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        return switch (currentStatus) {
            case "PENDING_PAYMENT" -> List.of("PAID", "CANCELLED").contains(newStatus);
            case "PAID" -> List.of("PROCESSING", "REFUNDING").contains(newStatus);
            case "PROCESSING" -> List.of("SHIPPED", "REFUNDING").contains(newStatus);
            case "SHIPPED" -> List.of("DELIVERED", "REFUNDING").contains(newStatus);
            case "DELIVERED" -> "COMPLETED".equals(newStatus);
            case "REFUNDING" -> "REFUNDED".equals(newStatus);
            default -> false;
        };
    }

    private OrderDetailResponse buildOrderDetailResponse(Order order, List<OrderItem> orderItems,
                                                          OrderAddress orderAddress,
                                                          List<OrderStatusHistory> statusHistoryList) {
        List<OrderDetailResponse.OrderItemResponse> itemResponses = orderItems.stream()
                .map(item -> OrderDetailResponse.OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        OrderDetailResponse.ShippingAddressResponse addressResponse = null;
        if (orderAddress != null) {
            addressResponse = OrderDetailResponse.ShippingAddressResponse.builder()
                    .receiverName(orderAddress.getReceiverName())
                    .phone(maskPhone(orderAddress.getPhone()))
                    .fullAddress(String.format("%s%s%s%s",
                            orderAddress.getProvince(),
                            orderAddress.getCity(),
                            orderAddress.getDistrict(),
                            orderAddress.getDetailAddress()))
                    .build();
        }

        List<OrderDetailResponse.StatusHistoryResponse> historyResponses = statusHistoryList.stream()
                .map(history -> OrderDetailResponse.StatusHistoryResponse.builder()
                        .status(history.getStatus())
                        .remark(history.getRemark())
                        .operator(history.getOperator())
                        .createdAt(history.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return OrderDetailResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .remark(order.getRemark())
                .items(itemResponses)
                .shippingAddress(addressResponse)
                .statusHistory(historyResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}