package com.example.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情实体类
 *
 * @author API Team
 * @version 1.0
 */
@Data
@TableName("order_items")
@Schema(description = "订单详情信息")
public class OrderItem {

    /**
     * 订单详情ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "订单详情ID")
    private Long id;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    @TableField("order_id")
    @Schema(description = "订单ID")
    private Long orderId;

    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空")
    @Size(max = 32, message = "订单编号长度不能超过32位")
    @TableField("order_no")
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    @TableField("product_id")
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200位")
    @TableField("product_name")
    @Schema(description = "商品名称")
    private String productName;

    /**
     * 商品图片
     */
    @Size(max = 500, message = "商品图片URL长度不能超过500位")
    @TableField("product_image")
    @Schema(description = "商品图片URL")
    private String productImage;

    /**
     * SKU ID
     */
    @TableField("sku_id")
    @Schema(description = "SKU ID")
    private Long skuId;

    /**
     * SKU编码
     */
    @Size(max = 64, message = "SKU编码长度不能超过64位")
    @TableField("sku_code")
    @Schema(description = "SKU编码")
    private String skuCode;

    /**
     * 商品单价
     */
    @NotNull(message = "商品单价不能为空")
    @DecimalMin(value = "0.00", message = "商品单价不能为负数")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @TableField("price")
    @Schema(description = "商品单价")
    private BigDecimal price;

    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    @Max(value = 9999, message = "购买数量不能超过9999")
    @TableField("quantity")
    @Schema(description = "购买数量")
    private Integer quantity;

    /**
     * 小计金额
     */
    @NotNull(message = "小计金额不能为空")
    @DecimalMin(value = "0.00", message = "小计金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @TableField("subtotal")
    @Schema(description = "小计金额")
    private BigDecimal subtotal;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 删除标记
     */
    @TableLogic
    @TableField("deleted")
    @Schema(description = "删除标记")
    private Integer deleted;
}
