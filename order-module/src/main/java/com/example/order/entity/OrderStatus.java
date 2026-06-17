package com.example.order.entity;

/**
 * 订单状态枚举
 *
 * @author API Team
 * @version 1.0
 */
public enum OrderStatus {

    /**
     * 待支付
     */
    PENDING("待支付"),

    /**
     * 已支付
     */
    PAID("已支付"),

    /**
     * 已发货
     */
    SHIPPED("已发货"),

    /**
     * 已送达
     */
    DELIVERED("已送达"),

    /**
     * 已取消
     */
    CANCELLED("已取消"),

    /**
     * 已退款
     */
    REFUNDED("已退款");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
