package com.md.basePlatform.domain;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 订单状态历史实体类
 */
@TableName("t_order_status_history")
public class OrderStatusHistory {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    public OrderStatusHistory() {}

    public OrderStatusHistory(Long id, Long orderId, String status, String remark, String operator, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.remark = remark;
        this.operator = operator;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long orderId;
        private String status;
        private String remark;
        private String operator;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder orderId(Long orderId) { this.orderId = orderId; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder remark(String remark) { this.remark = remark; return this; }
        public Builder operator(String operator) { this.operator = operator; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public OrderStatusHistory build() {
            return new OrderStatusHistory(id, orderId, status, remark, operator, createdAt);
        }
    }
}