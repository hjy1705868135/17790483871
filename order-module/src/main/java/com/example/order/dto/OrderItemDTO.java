package com.example.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单商品DTO
 *
 * @author API Team
 * @version 1.0
 */
@Data
@Schema(description = "订单商品信息")
public class OrderItemDTO {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID")
    private Long productId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200位")
    @Schema(description = "商品名称")
    private String productName;

    @Size(max = 500, message = "商品图片URL长度不能超过500位")
    @Schema(description = "商品图片URL")
    private String productImage;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Size(max = 64, message = "SKU编码长度不能超过64位")
    @Schema(description = "SKU编码")
    private String skuCode;

    @NotNull(message = "商品单价不能为空")
    @DecimalMin(value = "0.01", message = "商品单价必须大于0")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @Schema(description = "商品单价")
    private BigDecimal price;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    @Max(value = 9999, message = "购买数量不能超过9999")
    @Schema(description = "购买数量")
    private Integer quantity;
}
