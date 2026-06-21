package com.md.basePlatform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.md.basePlatform.common.ApiResponse;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<OrderDetailResponse> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                                        Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        OrderDetailResponse order = orderService.createOrder(request, userId);
        return ApiResponse.success("订单创建成功", order);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderDetailResponse> getOrderDetail(@PathVariable(value = "id") Long id,
                                                           Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        OrderDetailResponse order = orderService.getOrderDetail(id);
        
        // 验证订单所有权
        if (!order.getUserId().equals(userId)) {
            return ApiResponse.error(403, "无权访问该订单");
        }
        
        return ApiResponse.success(order);
    }

    /**
     * 分页查询订单列表
     */
    @GetMapping
    public ApiResponse<IPage<OrderSummaryResponse>> getOrders(
            @RequestParam(value = "page", defaultValue = "1") int pageNum,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "status", required = false) String status,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        IPage<OrderSummaryResponse> orders = orderService.getOrders(userId, status, pageNum, pageSize);
        return ApiResponse.success(orders);
    }

    /**
     * 更新订单状态（仅管理员）
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderDetailResponse> updateOrderStatus(
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {
        String operator = authentication.getName();
        OrderDetailResponse order = orderService.updateOrderStatus(id, request, operator);
        return ApiResponse.success("订单状态更新成功", order);
    }

    /**
     * 删除订单（仅管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteOrder(@PathVariable(value = "id") Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success("订单删除成功", null);
    }
}