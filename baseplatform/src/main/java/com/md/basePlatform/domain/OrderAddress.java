package com.md.basePlatform.domain;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 订单收货地址实体类
 */
@TableName("t_order_address")
public class OrderAddress {

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
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String phone;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 邮政编码
     */
    private String postalCode;

    public OrderAddress() {}

    public OrderAddress(Long id, Long orderId, String receiverName, String phone, String province, String city, String district, String detailAddress, String postalCode) {
        this.id = id;
        this.orderId = orderId;
        this.receiverName = receiverName;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detailAddress = detailAddress;
        this.postalCode = postalCode;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
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
        private Long id;
        private Long orderId;
        private String receiverName;
        private String phone;
        private String province;
        private String city;
        private String district;
        private String detailAddress;
        private String postalCode;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder orderId(Long orderId) { this.orderId = orderId; return this; }
        public Builder receiverName(String receiverName) { this.receiverName = receiverName; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder province(String province) { this.province = province; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder detailAddress(String detailAddress) { this.detailAddress = detailAddress; return this; }
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }

        public OrderAddress build() {
            return new OrderAddress(id, orderId, receiverName, phone, province, city, district, detailAddress, postalCode);
        }
    }
}