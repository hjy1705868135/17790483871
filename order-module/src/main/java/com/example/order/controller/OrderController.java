package com.example.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.order.dto.OrderCreateDTO;
import com.example.order.dto.OrderQueryDTO;
import com.example.order.dto.OrderUpdateDTO;
import com.example.order.exception.Result;
import com.example.order.service.OrderService;
import com.example.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 *
 * @author API Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "提供订单的CRUD操作接口")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新的订单")
    public Result<OrderVO> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        log.info("创建订单请求: {}", dto);
        OrderVO orderVO = orderService.createOrder(dto);
        return Result.success("订单创建成功", orderVO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详情")
    public Result<OrderVO> getOrderById(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("查询订单详情请求，订单ID: {}", id);
        OrderVO orderVO = orderService.getOrderById(id);
        return Result.success(orderVO);
    }

    @GetMapping("/no/{orderNo}")
    @Operation(summary = "查询订单详情", description = "根据订单编号查询订单详情")
    public Result<OrderVO> getOrderByOrderNo(
            @Parameter(description = "订单编号") @PathVariable String orderNo) {
        log.info("查询订单详情请求，订单编号: {}", orderNo);
        OrderVO orderVO = orderService.getOrderByOrderNo(orderNo);
        return Result.success(orderVO);
    }

    @GetMapping
    @Operation(summary = "分页查询订单列表", description = "支持多条件筛选、分页和排序")
    public Result<IPage<OrderVO>> queryOrderPage(OrderQueryDTO query) {
        log.info("分页查询订单列表请求: {}", query);
        IPage<OrderVO> page = orderService.queryOrderPage(query);
        return Result.success(page);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新订单信息", description = "更新订单的收货信息和备注")
    public Result<OrderVO> updateOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Valid @RequestBody OrderUpdateDTO dto) {
        log.info("更新订单请求，订单ID: {}, 数据: {}", id, dto);
        OrderVO orderVO = orderService.updateOrder(id, dto);
        return Result.success("订单更新成功", orderVO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单", description = "删除订单（逻辑删除）")
    public Result<Boolean> deleteOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("删除订单请求，订单ID: {}", id);
        boolean result = orderService.deleteOrder(id);
        return Result.success("订单删除成功", result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态", description = "更新订单状态")
    public Result<Boolean> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam String status) {
        log.info("更新订单状态请求，订单ID: {}, 新状态: {}", id, status);
        boolean result = orderService.updateOrderStatus(id, status);
        return Result.success("订单状态更新成功", result);
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单", description = "订单支付")
    public Result<Boolean> payOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "支付方式") @RequestParam String paymentMethod,
            @Parameter(description = "支付流水号") @RequestParam String paymentNo) {
        log.info("支付订单请求，订单ID: {}, 支付方式: {}, 支付流水号: {}", id, paymentMethod, paymentNo);
        boolean result = orderService.payOrder(id, paymentMethod, paymentNo);
        return Result.success("订单支付成功", result);
    }

    @PostMapping("/{id}/ship")
    @Operation(summary = "发货", description = "订单发货")
    public Result<Boolean> shipOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "快递公司") @RequestParam String expressCompany,
            @Parameter(description = "快递单号") @RequestParam String expressNo) {
        log.info("发货请求，订单ID: {}, 快递公司: {}, 快递单号: {}", id, expressCompany, expressNo);
        boolean result = orderService.shipOrder(id, expressCompany, expressNo);
        return Result.success("订单发货成功", result);
    }

    @PostMapping("/{id}/delivery")
    @Operation(summary = "确认收货", description = "确认收货")
    public Result<Boolean> confirmDelivery(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("确认收货请求，订单ID: {}", id);
        boolean result = orderService.confirmDelivery(id);
        return Result.success("确认收货成功", result);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消订单")
    public Result<Boolean> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        log.info("取消订单请求，订单ID: {}", id);
        boolean result = orderService.cancelOrder(id);
        return Result.success("订单取消成功", result);
    }
}
