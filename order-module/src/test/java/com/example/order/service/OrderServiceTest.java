package com.example.order.service;

import com.example.order.dto.OrderCreateDTO;
import com.example.order.dto.OrderQueryDTO;
import com.example.order.dto.OrderItemDTO;
import com.example.order.dto.OrderUpdateDTO;
import com.example.order.entity.Order;
import com.example.order.entity.OrderStatus;
import com.example.order.exception.BusinessException;
import com.example.order.vo.OrderVO;
import com.example.order.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单服务测试类
 *
 * @author API Team
 * @version 1.0
 */
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testCreateOrder() {
        // 准备测试数据
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setUserId(1001L);
        dto.setUserName("测试用户");
        dto.setTotalAmount(new BigDecimal("299.00"));
        dto.setDiscountAmount(new BigDecimal("20.00"));
        dto.setFreightAmount(new BigDecimal("10.00"));
        dto.setPayAmount(new BigDecimal("289.00"));
        dto.setReceiverName("张三");
        dto.setReceiverPhone("13800138001");
        dto.setReceiverAddress("北京市朝阳区XX街道XX号");

        List<OrderItemDTO> items = new ArrayList<>();
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(10001L);
        item.setProductName("iPhone 15 Pro");
        item.setPrice(new BigDecimal("7999.00"));
        item.setQuantity(1);
        items.add(item);
        dto.setItems(items);

        // 执行创建订单
        OrderVO orderVO = orderService.createOrder(dto);

        // 验证结果
        assertNotNull(orderVO);
        assertNotNull(orderVO.getId());
        assertNotNull(orderVO.getOrderNo());
        assertEquals("测试用户", orderVO.getUserName());
        assertEquals(OrderStatus.PENDING.getDescription(), orderVO.getStatusText());
        assertEquals(1, orderVO.getItems().size());
    }

    @Test
    void testGetOrderById() {
        // 先创建订单
        OrderCreateDTO dto = createTestOrder();
        OrderVO createdOrder = orderService.createOrder(dto);

        // 查询订单
        OrderVO orderVO = orderService.getOrderById(createdOrder.getId());

        // 验证结果
        assertNotNull(orderVO);
        assertEquals(createdOrder.getId(), orderVO.getId());
        assertEquals(createdOrder.getOrderNo(), orderVO.getOrderNo());
    }

    @Test
    void testQueryOrderPage() {
        // 创建多个订单
        for (int i = 0; i < 3; i++) {
            orderService.createOrder(createTestOrder());
        }

        // 分页查询
        OrderQueryDTO query = new OrderQueryDTO();
        query.setCurrent(1);
        query.setSize(10);
        IPage<OrderVO> page = orderService.queryOrderPage(query);

        // 验证结果
        assertNotNull(page);
        assertTrue(page.getTotal() >= 3);
        assertEquals(10, page.getSize());
    }

    @Test
    void testUpdateOrder() {
        // 先创建订单
        OrderCreateDTO createDto = createTestOrder();
        OrderVO createdOrder = orderService.createOrder(createDto);

        // 更新订单
        OrderUpdateDTO updateDto = new OrderUpdateDTO();
        updateDto.setReceiverName("李四");
        updateDto.setReceiverPhone("13900139002");
        updateDto.setReceiverAddress("上海市浦东新区XX路XX弄");
        updateDto.setRemark("测试更新备注");

        OrderVO updatedOrder = orderService.updateOrder(createdOrder.getId(), updateDto);

        // 验证结果
        assertNotNull(updatedOrder);
        assertEquals("李四", updatedOrder.getReceiverName());
        assertEquals("13900139002", updatedOrder.getReceiverPhone());
    }

    @Test
    void testPayOrder() {
        // 先创建订单
        OrderCreateDTO dto = createTestOrder();
        OrderVO createdOrder = orderService.createOrder(dto);

        // 支付订单
        boolean result = orderService.payOrder(createdOrder.getId(), "ALIPAY", "ZF20240110001");

        // 验证结果
        assertTrue(result);
        OrderVO paidOrder = orderService.getOrderById(createdOrder.getId());
        assertEquals(OrderStatus.PAID.getDescription(), paidOrder.getStatusText());
        assertNotNull(paidOrder.getPaymentTime());
    }

    @Test
    void testCancelOrder() {
        // 先创建订单
        OrderCreateDTO dto = createTestOrder();
        OrderVO createdOrder = orderService.createOrder(dto);

        // 取消订单
        boolean result = orderService.cancelOrder(createdOrder.getId());

        // 验证结果
        assertTrue(result);
        OrderVO cancelledOrder = orderService.getOrderById(createdOrder.getId());
        assertEquals(OrderStatus.CANCELLED.getDescription(), cancelledOrder.getStatusText());
    }

    @Test
    void testDeleteOrder() {
        // 先创建订单
        OrderCreateDTO dto = createTestOrder();
        OrderVO createdOrder = orderService.createOrder(dto);

        // 删除订单
        boolean result = orderService.deleteOrder(createdOrder.getId());

        // 验证结果
        assertTrue(result);
        assertThrows(BusinessException.class, () -> {
            orderService.getOrderById(createdOrder.getId());
        });
    }

    @Test
    void testShipOrder() {
        // 先创建并支付订单
        OrderCreateDTO dto = createTestOrder();
        OrderVO createdOrder = orderService.createOrder(dto);
        orderService.payOrder(createdOrder.getId(), "ALIPAY", "ZF20240110001");

        // 发货
        boolean result = orderService.shipOrder(createdOrder.getId(), "顺丰速运", "SF123456789");

        // 验证结果
        assertTrue(result);
        OrderVO shippedOrder = orderService.getOrderById(createdOrder.getId());
        assertEquals(OrderStatus.SHIPPED.getDescription(), shippedOrder.getStatusText());
        assertNotNull(shippedOrder.getShipTime());
    }

    @Test
    void testConfirmDelivery() {
        // 先创建、支付并发货订单
        OrderCreateDTO dto = createTestOrder();
        OrderVO createdOrder = orderService.createOrder(dto);
        orderService.payOrder(createdOrder.getId(), "ALIPAY", "ZF20240110001");
        orderService.shipOrder(createdOrder.getId(), "顺丰速运", "SF123456789");

        // 确认收货
        boolean result = orderService.confirmDelivery(createdOrder.getId());

        // 验证结果
        assertTrue(result);
        OrderVO deliveredOrder = orderService.getOrderById(createdOrder.getId());
        assertEquals(OrderStatus.DELIVERED.getDescription(), deliveredOrder.getStatusText());
        assertNotNull(deliveredOrder.getDeliveryTime());
    }

    /**
     * 创建测试订单数据
     */
    private OrderCreateDTO createTestOrder() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setUserId(1001L);
        dto.setUserName("测试用户");
        dto.setTotalAmount(new BigDecimal("299.00"));
        dto.setDiscountAmount(new BigDecimal("20.00"));
        dto.setFreightAmount(new BigDecimal("10.00"));
        dto.setPayAmount(new BigDecimal("289.00"));
        dto.setReceiverName("张三");
        dto.setReceiverPhone("13800138001");
        dto.setReceiverAddress("北京市朝阳区XX街道XX号");

        List<OrderItemDTO> items = new ArrayList<>();
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(10001L);
        item.setProductName("测试商品");
        item.setPrice(new BigDecimal("299.00"));
        item.setQuantity(1);
        items.add(item);
        dto.setItems(items);

        return dto;
    }
}
