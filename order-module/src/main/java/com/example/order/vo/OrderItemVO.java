package com.example.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品VO
 *
 * @author API Team
 * @version 1.0
 */
@Data
@Schema(description = "订单商品详情响应")
public class OrderItemVO {

    @Schema(description = "订单详情ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片URL")
    private String productImage;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SKU编码")
    private String skuCode;

    @Schema(description = "商品单价")
    private BigDecimal price;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "小计金额")
    private BigDecimal subtotal;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
