package com.example.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 *
 * @author API Team
 * @version 1.0
 */
@Data
@TableName("orders")
@Schema(description = "订单信息")
public class Order {

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "订单ID")
    private Long id;

    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空")
    @Size(max = 32, message = "订单编号长度不能超过32位")
    @TableField("order_no")
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 100, message = "用户名长度不能超过100位")
    @TableField("user_name")
    @Schema(description = "用户名")
    private String userName;

    /**
     * 订单总金额
     */
    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.00", message = "订单总金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @TableField("total_amount")
    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    @DecimalMin(value = "0.00", message = "优惠金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @TableField("discount_amount")
    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    /**
     * 运费金额
     */
    @DecimalMin(value = "0.00", message = "运费金额不能为负数")
    @Digits(integer = 8, fraction = 2, message = "金额格式不正确")
    @TableField("freight_amount")
    @Schema(description = "运费金额")
    private BigDecimal freightAmount;

    /**
     * 实际支付金额
     */
    @NotNull(message = "实际支付金额不能为空")
    @DecimalMin(value = "0.00", message = "实际支付金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    @TableField("pay_amount")
    @Schema(description = "实际支付金额")
    private BigDecimal payAmount;

    /**
     * 订单状态
     */
    @NotBlank(message = "订单状态不能为空")
    @TableField("status")
    @Schema(description = "订单状态")
    private String status;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50位")
    @TableField("receiver_name")
    @Schema(description = "收货人姓名")
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @TableField("receiver_phone")
    @Schema(description = "收货人电话")
    private String receiverPhone;

    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    @Size(max = 255, message = "收货地址长度不能超过255位")
    @TableField("receiver_address")
    @Schema(description = "收货地址")
    private String receiverAddress;

    /**
     * 邮政编码
     */
    @Size(max = 10, message = "邮政编码长度不能超过10位")
    @TableField("receiver_postal_code")
    @Schema(description = "邮政编码")
    private String receiverPostalCode;

    /**
     * 支付方式
     */
    @TableField("payment_method")
    @Schema(description = "支付方式")
    private String paymentMethod;

    /**
     * 支付时间
     */
    @TableField("payment_time")
    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    /**
     * 支付流水号
     */
    @Size(max = 64, message = "支付流水号长度不能超过64位")
    @TableField("payment_no")
    @Schema(description = "支付流水号")
    private String paymentNo;

    /**
     * 快递公司
     */
    @Size(max = 50, message = "快递公司名称长度不能超过50位")
    @TableField("express_company")
    @Schema(description = "快递公司")
    private String expressCompany;

    /**
     * 快递单号
     */
    @Size(max = 50, message = "快递单号长度不能超过50位")
    @TableField("express_no")
    @Schema(description = "快递单号")
    private String expressNo;

    /**
     * 发货时间
     */
    @TableField("ship_time")
    @Schema(description = "发货时间")
    private LocalDateTime shipTime;

    /**
     * 收货时间
     */
    @TableField("delivery_time")
    @Schema(description = "收货时间")
    private LocalDateTime deliveryTime;

    /**
     * 订单备注
     */
    @Size(max = 500, message = "订单备注长度不能超过500位")
    @TableField("remark")
    @Schema(description = "订单备注")
    private String remark;

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

    /**
     * 订单详情列表（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "订单详情列表")
    private List<OrderItem> items;
}
