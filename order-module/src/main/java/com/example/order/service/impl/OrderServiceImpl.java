package com.example.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.order.dto.OrderCreateDTO;
import com.example.order.dto.OrderQueryDTO;
import com.example.order.dto.OrderUpdateDTO;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.entity.OrderStatus;
import com.example.order.exception.BusinessException;
import com.example.order.mapper.OrderItemMapper;
import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderService;
import com.example.order.vo.OrderItemVO;
import com.example.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 *
 * @author API Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateDTO dto) {
        log.info("创建订单，用户ID: {}", dto.getUserId());

        // 生成订单编号
        String orderNo = generateOrderNo();

        // 创建订单实体
        Order order = new Order();
        BeanUtil.copyProperties(dto, order);
        order.setOrderNo(orderNo);
        order.setStatus(OrderStatus.PENDING.name());
        order.setDiscountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO);
        order.setFreightAmount(dto.getFreightAmount() != null ? dto.getFreightAmount() : BigDecimal.ZERO);

        // 保存订单
        orderMapper.insert(order);

        // 创建订单详情列表
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < dto.getItems().size(); i++) {
            OrderItem item = new OrderItem();
            BeanUtil.copyProperties(dto.getItems().get(i), item);
            item.setId(IdUtil.getSnowflakeNextId());
            item.setOrderId(order.getId());
            item.setOrderNo(orderNo);
            item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setCreatedAt(LocalDateTime.now());
            item.setUpdatedAt(LocalDateTime.now());
            item.setDeleted(0);
            orderItems.add(item);
        }

        // 批量保存订单详情
        orderItemMapper.batchInsert(orderItems);

        log.info("订单创建成功，订单ID: {}, 订单编号: {}", order.getId(), orderNo);

        return getOrderById(order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO updateOrder(Long id, OrderUpdateDTO dto) {
        log.info("更新订单，订单ID: {}", id);

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态，只有待支付状态可以更新
        if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            throw new BusinessException("只有待支付状态的订单才能修改");
        }

        // 更新订单信息
        BeanUtil.copyProperties(dto, order);
        orderMapper.updateById(order);

        log.info("订单更新成功，订单ID: {}", id);

        return getOrderById(id);
    }

    @Override
    public OrderVO getOrderById(Long id) {
        log.info("查询订单详情，订单ID: {}", id);

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        return convertToVO(order);
    }

    @Override
    public OrderVO getOrderByOrderNo(String orderNo) {
        log.info("查询订单详情，订单编号: {}", orderNo);

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        return convertToVO(order);
    }

    @Override
    public IPage<OrderVO> queryOrderPage(OrderQueryDTO query) {
        log.info("分页查询订单，当前页: {}, 每页数量: {}", query.getCurrent(), query.getSize());

        // 设置默认分页参数
        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        }
        if (query.getSize() > 100) {
            query.setSize(100);
        }

        // 执行分页查询
        Page<Order> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<Order> orderPage = orderMapper.selectOrderPage(page, query);

        // 转换为VO
        return orderPage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(Long id) {
        log.info("删除订单，订单ID: {}", id);

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态，只有待支付和已取消状态可以删除
        if (!OrderStatus.PENDING.name().equals(order.getStatus())
                && !OrderStatus.CANCELLED.name().equals(order.getStatus())) {
            throw new BusinessException("只有待支付或已取消状态的订单才能删除");
        }

        // 逻辑删除订单
        orderMapper.deleteById(id);

        // 逻辑删除订单详情
        orderItemMapper.deleteByOrderId(id);

        log.info("订单删除成功，订单ID: {}", id);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(Long id, String status) {
        log.info("更新订单状态，订单ID: {}, 新状态: {}", id, status);

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证状态值
        try {
            OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("无效的订单状态");
        }

        // 更新状态
        order.setStatus(status);
        orderMapper.updateById(order);

        log.info("订单状态更新成功，订单ID: {}, 新状态: {}", id, status);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long id, String paymentMethod, String paymentNo) {
        log.info("支付订单，订单ID: {}, 支付方式: {}, 支付流水号: {}", id, paymentMethod, paymentNo);

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            throw new BusinessException("只有待支付的订单才能支付");
        }

        // 更新支付信息
        order.setPaymentMethod(paymentMethod);
        order.setPaymentNo(paymentNo);
        order.setPaymentTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID.name());
        orderMapper.updateById(order);

        log.info("订单支付成功，订单ID: {}", id);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shipOrder(Long id, String expressCompany, String expressNo) {
        log.info("发货，订单ID: {}, 快递公司: {}, 快递单号: {}", id, expressCompany, expressNo);

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (!OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new BusinessException("只有已支付的订单才能发货");
        }

        // 更新发货信息
        order.setExpressCompany(expressCompany);
        order.setExpressNo(expressNo);
        order.setShipTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SHIPPED.name());
        orderMapper.updateById(order);

        log.info("订单发货成功，订单ID: {}", id);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmDelivery(Long id) {
        log.info("确认收货，订单ID: {}", id);

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (!OrderStatus.SHIPPED.name().equals(order.getStatus())) {
            throw new BusinessException("只有已发货的订单才能确认收货");
        }

        // 更新收货信息
        order.setDeliveryTime(LocalDateTime.now());
        order.setStatus(OrderStatus.DELIVERED.name());
        orderMapper.updateById(order);

        log.info("订单确认收货成功，订单ID: {}", id);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long id) {
        log.info("取消订单，订单ID: {}", id);

        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态，只有待支付状态可以取消
        if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            throw new BusinessException("只有待支付的订单才能取消");
        }

        // 更新状态
        order.setStatus(OrderStatus.CANCELLED.name());
        orderMapper.updateById(order);

        log.info("订单取消成功，订单ID: {}", id);

        return true;
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + IdUtil.getSnowflakeNextIdStr().substring(16);
    }

    /**
     * 转换为VO对象
     */
    private OrderVO convertToVO(Order order) {
        if (order == null) {
            return null;
        }

        OrderVO vo = new OrderVO();
        BeanUtil.copyProperties(order, vo);

        // 设置状态描述
        try {
            OrderStatus status = OrderStatus.valueOf(order.getStatus());
            vo.setStatusText(status.getDescription());
        } catch (IllegalArgumentException e) {
            vo.setStatusText(order.getStatus());
        }

        // 查询订单详情列表
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        if (orderItems != null && !orderItems.isEmpty()) {
            vo.setItems(orderItems.stream().map(this::convertToItemVO).collect(Collectors.toList()));
        }

        return vo;
    }

    /**
     * 转换为订单详情VO
     */
    private OrderItemVO convertToItemVO(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemVO vo = new OrderItemVO();
        BeanUtil.copyProperties(item, vo);
        return vo;
    }
}
