package com.example.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单创建DTO
 *
 * @author API Team
 * @version 1.0
 */
@Data
@Schema(description = "订单创建请求")
public class OrderCreateDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

    @NotBlank(message = "用户名不能为空")
    @Size(max = 100, message = "用户名长度不能超过100位")
    @Schema(description = "用户名")
    private String userName;

    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.01", message = "订单总金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.00", message = "优惠金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.00", message = "运费金额不能为负数")
    @Digits(integer = 8, fraction = 2, message = "金额格式不正确")
    @Schema(description = "运费金额")
    private BigDecimal freightAmount;

    @NotNull(message = "实际支付金额不能为空")
    @DecimalMin(value = "0.01", message = "实际支付金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @Schema(description = "实际支付金额")
    private BigDecimal payAmount;

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50位")
    @Schema(description = "收货人姓名")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "收货人电话")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    @Size(max = 255, message = "收货地址长度不能超过255位")
    @Schema(description = "收货地址")
    private String receiverAddress;

    @Size(max = 10, message = "邮政编码长度不能超过10位")
    @Schema(description = "邮政编码")
    private String receiverPostalCode;

    @Size(max = 500, message = "订单备注长度不能超过500位")
    @Schema(description = "订单备注")
    private String remark;

    @NotNull(message = "订单商品列表不能为空")
    @Size(min = 1, message = "订单至少包含一个商品")
    @Schema(description = "订单商品列表")
    private java.util.List<OrderItemDTO> items;
}
