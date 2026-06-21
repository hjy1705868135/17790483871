package com.md.basePlatform.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情响应DTO
 */
public class OrderDetailResponse {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private java.math.BigDecimal totalAmount;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付状态
     */
    private String paymentStatus;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 订单商品列表
     */
    private List<OrderItemResponse> items;

    /**
     * 收货地址
     */
    private ShippingAddressResponse shippingAddress;

    /**
     * 状态历史
     */
    private List<StatusHistoryResponse> statusHistory;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    public OrderDetailResponse() {}

    public OrderDetailResponse(Long id, String orderNo, Long userId, java.math.BigDecimal totalAmount, String status, String paymentMethod, String paymentStatus, String remark, List<OrderItemResponse> items, ShippingAddressResponse shippingAddress, List<StatusHistoryResponse> statusHistory, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.remark = remark;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.statusHistory = statusHistory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public java.math.BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(java.math.BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
    public ShippingAddressResponse getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddressResponse shippingAddress) { this.shippingAddress = shippingAddress; }
    public List<StatusHistoryResponse> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<StatusHistoryResponse> statusHistory) { this.statusHistory = statusHistory; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String orderNo;
        private Long userId;
        private java.math.BigDecimal totalAmount;
        private String status;
        private String paymentMethod;
        private String paymentStatus;
        private String remark;
        private List<OrderItemResponse> items;
        private ShippingAddressResponse shippingAddress;
        private List<StatusHistoryResponse> statusHistory;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder orderNo(String orderNo) { this.orderNo = orderNo; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder totalAmount(java.math.BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder paymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public Builder remark(String remark) { this.remark = remark; return this; }
        public Builder items(List<OrderItemResponse> items) { this.items = items; return this; }
        public Builder shippingAddress(ShippingAddressResponse shippingAddress) { this.shippingAddress = shippingAddress; return this; }
        public Builder statusHistory(List<StatusHistoryResponse> statusHistory) { this.statusHistory = statusHistory; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public OrderDetailResponse build() {
            return new OrderDetailResponse(id, orderNo, userId, totalAmount, status, paymentMethod, paymentStatus, remark, items, shippingAddress, statusHistory, createdAt, updatedAt);
        }
    }

    /**
     * 订单商品响应DTO
     */
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private Integer quantity;
        private java.math.BigDecimal price;
        private java.math.BigDecimal subtotal;

        public OrderItemResponse() {}

        public OrderItemResponse(Long id, Long productId, String productName, String productImage, Integer quantity, java.math.BigDecimal price, java.math.BigDecimal subtotal) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.productImage = productImage;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = subtotal;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getProductImage() { return productImage; }
        public void setProductImage(String productImage) { this.productImage = productImage; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public java.math.BigDecimal getPrice() { return price; }
        public void setPrice(java.math.BigDecimal price) { this.price = price; }
        public java.math.BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(java.math.BigDecimal subtotal) { this.subtotal = subtotal; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long id;
            private Long productId;
            private String productName;
            private String productImage;
            private Integer quantity;
            private java.math.BigDecimal price;
            private java.math.BigDecimal subtotal;

            public Builder id(Long id) { this.id = id; return this; }
            public Builder productId(Long productId) { this.productId = productId; return this; }
            public Builder productName(String productName) { this.productName = productName; return this; }
            public Builder productImage(String productImage) { this.productImage = productImage; return this; }
            public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
            public Builder price(java.math.BigDecimal price) { this.price = price; return this; }
            public Builder subtotal(java.math.BigDecimal subtotal) { this.subtotal = subtotal; return this; }

            public OrderItemResponse build() {
                return new OrderItemResponse(id, productId, productName, productImage, quantity, price, subtotal);
            }
        }
    }

    /**
     * 收货地址响应DTO
     */
    public static class ShippingAddressResponse {
        private String receiverName;
        private String phone;
        private String fullAddress;

        public ShippingAddressResponse() {}

        public ShippingAddressResponse(String receiverName, String phone, String fullAddress) {
            this.receiverName = receiverName;
            this.phone = phone;
            this.fullAddress = fullAddress;
        }

        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getFullAddress() { return fullAddress; }
        public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String receiverName;
            private String phone;
            private String fullAddress;

            public Builder receiverName(String receiverName) { this.receiverName = receiverName; return this; }
            public Builder phone(String phone) { this.phone = phone; return this; }
            public Builder fullAddress(String fullAddress) { this.fullAddress = fullAddress; return this; }

            public ShippingAddressResponse build() {
                return new ShippingAddressResponse(receiverName, phone, fullAddress);
            }
        }
    }

    /**
     * 状态历史响应DTO
     */
    public static class StatusHistoryResponse {
        private String status;
        private String remark;
        private String operator;
        private LocalDateTime createdAt;

        public StatusHistoryResponse() {}

        public StatusHistoryResponse(String status, String remark, String operator, LocalDateTime createdAt) {
            this.status = status;
            this.remark = remark;
            this.operator = operator;
            this.createdAt = createdAt;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String status;
            private String remark;
            private String operator;
            private LocalDateTime createdAt;

            public Builder status(String status) { this.status = status; return this; }
            public Builder remark(String remark) { this.remark = remark; return this; }
            public Builder operator(String operator) { this.operator = operator; return this; }
            public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

            public StatusHistoryResponse build() {
                return new StatusHistoryResponse(status, remark, operator, createdAt);
            }
        }
    }
}