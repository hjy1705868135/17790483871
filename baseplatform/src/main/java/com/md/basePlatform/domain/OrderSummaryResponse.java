package com.md.basePlatform.domain;

import java.time.LocalDateTime;

/**
 * 订单摘要响应DTO
 */
public class OrderSummaryResponse {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单总金额
     */
    private java.math.BigDecimal totalAmount;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 商品数量
     */
    private Integer itemCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    public OrderSummaryResponse() {}

    public OrderSummaryResponse(Long id, String orderNo, java.math.BigDecimal totalAmount, String status, Integer itemCount, LocalDateTime createdAt) {
        this.id = id;
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.status = status;
        this.itemCount = itemCount;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public java.math.BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(java.math.BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String orderNo;
        private java.math.BigDecimal totalAmount;
        private String status;
        private Integer itemCount;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder orderNo(String orderNo) { this.orderNo = orderNo; return this; }
        public Builder totalAmount(java.math.BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder itemCount(Integer itemCount) { this.itemCount = itemCount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public OrderSummaryResponse build() {
            return new OrderSummaryResponse(id, orderNo, totalAmount, status, itemCount, createdAt);
        }
    }
}