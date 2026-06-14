package com.example.order.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.order.dto.OrderCreateDTO;
import com.example.order.dto.OrderItemDTO;
import com.example.order.dto.OrderQueryDTO;
import com.example.order.dto.OrderUpdateDTO;
import com.example.order.entity.OrderStatus;
import com.example.order.vo.OrderVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单控制器测试类
 *
 * @author API Team
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderVO testOrder;

    @BeforeEach
    void setUp() throws Exception {
        // 创建测试订单
        OrderCreateDTO dto = createTestOrder();
        String json = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject dataObject = jsonObject.getJSONObject("data");

        testOrder = new OrderVO();
        testOrder.setId(dataObject.getLong("id"));
        testOrder.setOrderNo(dataObject.getStr("orderNo"));
    }

    @Test
    void testCreateOrder() throws Exception {
        OrderCreateDTO dto = createTestOrder();
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderNo").exists())
                .andExpect(jsonPath("$.data.statusText").value("待支付"));
    }

    @Test
    void testGetOrderById() throws Exception {
        mockMvc.perform(get("/api/orders/" + testOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testOrder.getId()))
                .andExpect(jsonPath("$.data.orderNo").value(testOrder.getOrderNo()));
    }

    @Test
    void testGetOrderByOrderNo() throws Exception {
        mockMvc.perform(get("/api/orders/no/" + testOrder.getOrderNo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderNo").value(testOrder.getOrderNo()));
    }

    @Test
    void testQueryOrderPage() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .param("current", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").isNumber());
    }

    @Test
    void testUpdateOrder() throws Exception {
        OrderUpdateDTO updateDto = new OrderUpdateDTO();
        updateDto.setReceiverName("李四");
        updateDto.setReceiverPhone("13900139002");
        updateDto.setReceiverAddress("上海市浦东新区XX路XX弄");
        updateDto.setRemark("测试更新备注");

        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/orders/" + testOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.receiverName").value("李四"))
                .andExpect(jsonPath("$.data.receiverPhone").value("13900139002"));
    }

    @Test
    void testPayOrder() throws Exception {
        mockMvc.perform(post("/api/orders/" + testOrder.getId() + "/pay")
                        .param("paymentMethod", "ALIPAY")
                        .param("paymentNo", "ZF20240110001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testCancelOrder() throws Exception {
        mockMvc.perform(post("/api/orders/" + testOrder.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testDeleteOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/" + testOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testShipOrder() throws Exception {
        // 先支付订单
        mockMvc.perform(post("/api/orders/" + testOrder.getId() + "/pay")
                        .param("paymentMethod", "ALIPAY")
                        .param("paymentNo", "ZF20240110001"));

        // 发货
        mockMvc.perform(post("/api/orders/" + testOrder.getId() + "/ship")
                        .param("expressCompany", "顺丰速运")
                        .param("expressNo", "SF123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testConfirmDelivery() throws Exception {
        // 先支付订单
        mockMvc.perform(post("/api/orders/" + testOrder.getId() + "/pay")
                        .param("paymentMethod", "ALIPAY")
                        .param("paymentNo", "ZF20240110001"));

        // 发货
        mockMvc.perform(post("/api/orders/" + testOrder.getId() + "/ship")
                        .param("expressCompany", "顺丰速运")
                        .param("expressNo", "SF123456789"));

        // 确认收货
        mockMvc.perform(post("/api/orders/" + testOrder.getId() + "/delivery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
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
