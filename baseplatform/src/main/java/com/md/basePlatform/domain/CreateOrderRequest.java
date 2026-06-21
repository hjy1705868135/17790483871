package com.md.basePlatform.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.ArrayList;

/**
 * 创建订单请求DTO
 */
public class CreateOrderRequest {

    /**
     * 收货地址
     */
    @NotNull(message = "收货地址不能为空")
    @Valid
    private ShippingAddressDTO shippingAddress;

    /**
     * 支付方式
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    /**
     * 订单商品列表
     */
    @NotEmpty(message = "订单商品不能为空")
    @Size(min = 1, message = "至少需要一个商品")
    @Valid
    private List<OrderItemRequest> items;

    /**
     * 订单备注
     */
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    public CreateOrderRequest() {
        this.items = new ArrayList<>();
    }

    public CreateOrderRequest(ShippingAddressDTO shippingAddress, String paymentMethod, List<OrderItemRequest> items, String remark) {
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.remark = remark;
    }

    public ShippingAddressDTO getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddressDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ShippingAddressDTO shippingAddress;
        private String paymentMethod;
        private List<OrderItemRequest> items;
        private String remark;

        public Builder shippingAddress(ShippingAddressDTO shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder items(List<OrderItemRequest> items) {
            this.items = items;
            return this;
        }

        public Builder remark(String remark) {
            this.remark = remark;
            return this;
        }

        public CreateOrderRequest build() {
            return new CreateOrderRequest(shippingAddress, paymentMethod, items, remark);
        }
    }

    /**
     * 收货地址DTO
     */
    public static class ShippingAddressDTO {
        @NotBlank(message = "收货人姓名不能为空")
        private String receiverName;

        @NotBlank(message = "收货人电话不能为空")
        private String phone;

        @NotBlank(message = "省份不能为空")
        private String province;

        @NotBlank(message = "城市不能为空")
        private String city;

        @NotBlank(message = "区不能为空")
        private String district;

        @NotBlank(message = "详细地址不能为空")
        private String detailAddress;

        private String postalCode;

        public ShippingAddressDTO() {}

        public ShippingAddressDTO(String receiverName, String phone, String province, String city, String district, String detailAddress, String postalCode) {
            this.receiverName = receiverName;
            this.phone = phone;
            this.province = province;
            this.city = city;
            this.district = district;
            this.detailAddress = detailAddress;
            this.postalCode = postalCode;
        }

        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        public String getDetailAddress() { return detailAddress; }
        public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String receiverName;
            private String phone;
            private String province;
            private String city;
            private String district;
            private String detailAddress;
            private String postalCode;

            public Builder receiverName(String receiverName) { this.receiverName = receiverName; return this; }
            public Builder phone(String phone) { this.phone = phone; return this; }
            public Builder province(String province) { this.province = province; return this; }
            public Builder city(String city) { this.city = city; return this; }
            public Builder district(String district) { this.district = district; return this; }
            public Builder detailAddress(String detailAddress) { this.detailAddress = detailAddress; return this; }
            public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }

            public ShippingAddressDTO build() {
                return new ShippingAddressDTO(receiverName, phone, province, city, district, detailAddress, postalCode);
            }
        }
    }

    /**
     * 订单商品请求DTO
     */
    public static class OrderItemRequest {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "购买数量不能为空")
        private Integer quantity;

        @NotNull(message = "商品单价不能为空")
        private java.math.BigDecimal price;

        public OrderItemRequest() {}

        public OrderItemRequest(Long productId, Integer quantity, java.math.BigDecimal price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public java.math.BigDecimal getPrice() { return price; }
        public void setPrice(java.math.BigDecimal price) { this.price = price; }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long productId;
            private Integer quantity;
            private java.math.BigDecimal price;

            public Builder productId(Long productId) { this.productId = productId; return this; }
            public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
            public Builder price(java.math.BigDecimal price) { this.price = price; return this; }

            public OrderItemRequest build() {
                return new OrderItemRequest(productId, quantity, price);
            }
        }
    }
}