package com.md.basePlatform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.exception.OrderException;
import com.md.basePlatform.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * OrderService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderAddressRepository orderAddressRepository;

    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 100L;
    private static final Long PRODUCT_ID = 10L;
    private Product mockProduct;
    private CreateOrderRequest createOrderRequest;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockProduct = Product.builder()
                .id(PRODUCT_ID).name("测试商品").price(new BigDecimal("99.00"))
                .stock(50).enabled(1).image("test.jpg").brand("测试品牌")
                .spec("规格A").categoryId(1L)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        CreateOrderRequest.ShippingAddressDTO shippingAddress = new CreateOrderRequest.ShippingAddressDTO();
        shippingAddress.setReceiverName("张三");
        shippingAddress.setPhone("13800138000");
        shippingAddress.setProvince("北京市");
        shippingAddress.setCity("北京市");
        shippingAddress.setDistrict("朝阳区");
        shippingAddress.setDetailAddress("某某街道123号");
        shippingAddress.setPostalCode("100000");

        CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest();
        orderItem.setProductId(PRODUCT_ID);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("99.00"));

        createOrderRequest = CreateOrderRequest.builder()
                .shippingAddress(shippingAddress)
                .paymentMethod("ALIPAY")
                .items(Collections.singletonList(orderItem))
                .remark("测试订单")
                .build();

        mockOrder = Order.builder()
                .id(ORDER_ID).orderNo("ORD202606170001").userId(USER_ID)
                .totalAmount(new BigDecimal("198.00")).status("PENDING_PAYMENT")
                .paymentMethod("ALIPAY").paymentStatus("UNPAID").remark("测试订单")
                .deleted(0).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
    }

    // ============ 创建订单测试 ============

    @Test
    void should_CreateOrderSuccessfully_When_StockIsSufficient() {
        // Given
        when(orderRepository.insert(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(ORDER_ID);
            return 1;
        });
        when(productRepository.selectById(PRODUCT_ID)).thenReturn(mockProduct);
        when(productRepository.updateById(any(Product.class))).thenReturn(1);
        when(orderItemRepository.insert(any(OrderItem.class))).thenReturn(1);
        when(orderAddressRepository.insert(any(OrderAddress.class))).thenReturn(1);
        when(orderStatusHistoryRepository.insert(any(OrderStatusHistory.class))).thenReturn(1);

        // 模拟 getOrderDetail 的调用链
        when(orderRepository.selectById(ORDER_ID)).thenReturn(mockOrder);
        when(orderItemRepository.selectList(any())).thenReturn(Collections.emptyList());
        when(orderAddressRepository.selectOne(any())).thenReturn(null);
        when(orderStatusHistoryRepository.selectList(any())).thenReturn(Collections.emptyList());

        // When
        OrderDetailResponse response = orderService.createOrder(createOrderRequest, USER_ID);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(ORDER_ID);
        assertThat(response.getOrderNo()).isEqualTo("ORD202606170001");
        assertThat(response.getStatus()).isEqualTo("PENDING_PAYMENT");

        verify(orderRepository).insert(any(Order.class));
        verify(orderItemRepository, atLeastOnce()).insert(any(OrderItem.class));
        verify(orderAddressRepository).insert(any(OrderAddress.class));
        verify(orderStatusHistoryRepository).insert(any(OrderStatusHistory.class));
        verify(productRepository).updateById(any(Product.class));
    }

    @Test
    void should_ThrowOrderException_When_ProductNotFound() {
        // Given
        when(orderRepository.insert(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(ORDER_ID);
            return 1;
        });
        when(productRepository.selectById(PRODUCT_ID)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(createOrderRequest, USER_ID))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("商品不存在");
    }

    @Test
    void should_ThrowOrderException_When_StockIsInsufficient() {
        // Given
        Product lowStockProduct = Product.builder()
                .id(PRODUCT_ID).name("测试商品").price(new BigDecimal("99.00"))
                .stock(1).enabled(1).image("test.jpg").brand("测试品牌")
                .spec("规格A").categoryId(1L)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(orderRepository.insert(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(ORDER_ID);
            return 1;
        });
        when(productRepository.selectById(PRODUCT_ID)).thenReturn(lowStockProduct);

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(createOrderRequest, USER_ID))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("库存不足");
    }

    // ============ 获取订单详情测试 ============

    @Test
    void should_GetOrderDetailSuccessfully_When_OrderExists() {
        // Given
        when(orderRepository.selectById(ORDER_ID)).thenReturn(mockOrder);
        when(orderItemRepository.selectList(any())).thenReturn(Collections.emptyList());

        OrderAddress mockAddress = OrderAddress.builder()
                .id(1L).orderId(ORDER_ID).receiverName("张三").phone("13800138000")
                .province("北京市").city("北京市").district("朝阳区")
                .detailAddress("某某街道123号").postalCode("100000").build();
        when(orderAddressRepository.selectOne(any())).thenReturn(mockAddress);

        when(orderStatusHistoryRepository.selectList(any())).thenReturn(Collections.emptyList());

        // When
        OrderDetailResponse response = orderService.getOrderDetail(ORDER_ID);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(ORDER_ID);
        assertThat(response.getOrderNo()).isEqualTo("ORD202606170001");
        assertThat(response.getStatus()).isEqualTo("PENDING_PAYMENT");
    }

    @Test
    void should_ThrowOrderException_When_OrderNotFound() {
        // Given
        when(orderRepository.selectById(ORDER_ID)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderDetail(ORDER_ID))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("订单不存在");
    }

    // ============ 分页查询订单测试 ============

    @Test
    void should_ReturnOrderList_When_UserHasOrders() {
        // Given
        Page<Order> orderPage = new Page<>(1, 10);
        orderPage.setRecords(Collections.singletonList(mockOrder));
        orderPage.setTotal(1);

        when(orderRepository.selectUserOrders(any(Page.class), eq(USER_ID), eq(null))).thenReturn(orderPage);
        when(orderItemRepository.selectCount(any())).thenReturn(2L);

        // When
        IPage<OrderSummaryResponse> result = orderService.getOrders(USER_ID, null, 1, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().get(0).getOrderNo()).isEqualTo("ORD202606170001");
    }

    // ============ 更新订单状态测试 ============

    @Test
    void should_UpdateOrderStatusSuccessfully_When_TransitionIsValid() {
        // Given
        UpdateOrderStatusRequest statusRequest = UpdateOrderStatusRequest.builder()
                .status("PAID").remark("用户支付成功").build();

        when(orderRepository.selectById(ORDER_ID)).thenReturn(mockOrder);
        when(orderRepository.updateById(any(Order.class))).thenReturn(1);
        when(orderStatusHistoryRepository.insert(any(OrderStatusHistory.class))).thenReturn(1);

        // getOrderDetail 调用
        when(orderItemRepository.selectList(any())).thenReturn(Collections.emptyList());
        when(orderAddressRepository.selectOne(any())).thenReturn(null);
        when(orderStatusHistoryRepository.selectList(any())).thenReturn(Collections.emptyList());

        // When
        OrderDetailResponse response = orderService.updateOrderStatus(ORDER_ID, statusRequest, "admin");

        // Then
        assertThat(response).isNotNull();
        verify(orderRepository).updateById(any(Order.class));
        verify(orderStatusHistoryRepository).insert(any(OrderStatusHistory.class));
    }

    @Test
    void should_ThrowOrderException_When_StatusTransitionIsInvalid() {
        // Given
        UpdateOrderStatusRequest statusRequest = UpdateOrderStatusRequest.builder()
                .status("COMPLETED").remark("尝试非法状态变更").build();

        when(orderRepository.selectById(ORDER_ID)).thenReturn(mockOrder);

        // When & Then
        assertThatThrownBy(() -> orderService.updateOrderStatus(ORDER_ID, statusRequest, "admin"))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("无效的状态转换");
    }

    // ============ 删除订单测试 ============

    @Test
    void should_DeleteOrderSuccessfully_When_OrderCanBeDeleted() {
        // Given
        when(orderRepository.selectById(ORDER_ID)).thenReturn(mockOrder);
        when(orderRepository.updateById(any(Order.class))).thenReturn(1);

        // When
        orderService.deleteOrder(ORDER_ID);

        // Then
        verify(orderRepository).updateById(any(Order.class));
    }

    @Test
    void should_ThrowOrderException_When_DeletingCompletedOrder() {
        // Given
        Order completedOrder = Order.builder()
                .id(ORDER_ID).orderNo("ORD202606170001").userId(USER_ID)
                .totalAmount(new BigDecimal("198.00")).status("COMPLETED")
                .deleted(0).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(orderRepository.selectById(ORDER_ID)).thenReturn(completedOrder);

        // When & Then
        assertThatThrownBy(() -> orderService.deleteOrder(ORDER_ID))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("已完成订单不能删除");
    }
}
