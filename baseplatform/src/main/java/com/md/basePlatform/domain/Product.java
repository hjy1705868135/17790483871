package com.md.basePlatform.domain;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@TableName("t_product")
public class Product {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 折扣
     */
    private Integer discount;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 评分
     */
    private Double rating;

    /**
     * 评价数量
     */
    private Integer reviewCount;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 规格
     */
    private String spec;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 闪购结束时间
     */
    private LocalDateTime flashSaleEndTime;

    /**
     * 是否上架
     */
    private Integer enabled;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public Product() {}

    public Product(Long id, String name, String description, String image, BigDecimal price, BigDecimal originalPrice, Integer discount, Integer sales, Double rating, Integer reviewCount, Integer stock, String brand, String spec, Long categoryId, LocalDateTime flashSaleEndTime, Integer enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.sales = sales;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.stock = stock;
        this.brand = brand;
        this.spec = spec;
        this.categoryId = categoryId;
        this.flashSaleEndTime = flashSaleEndTime;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public Integer getDiscount() { return discount; }
    public void setDiscount(Integer discount) { this.discount = discount; }
    public Integer getSales() { return sales; }
    public void setSales(Integer sales) { this.sales = sales; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public LocalDateTime getFlashSaleEndTime() { return flashSaleEndTime; }
    public void setFlashSaleEndTime(LocalDateTime flashSaleEndTime) { this.flashSaleEndTime = flashSaleEndTime; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String image;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer discount;
        private Integer sales;
        private Double rating;
        private Integer reviewCount;
        private Integer stock;
        private String brand;
        private String spec;
        private Long categoryId;
        private LocalDateTime flashSaleEndTime;
        private Integer enabled;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder image(String image) { this.image = image; return this; }
        public Builder price(BigDecimal price) { this.price = price; return this; }
        public Builder originalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; return this; }
        public Builder discount(Integer discount) { this.discount = discount; return this; }
        public Builder sales(Integer sales) { this.sales = sales; return this; }
        public Builder rating(Double rating) { this.rating = rating; return this; }
        public Builder reviewCount(Integer reviewCount) { this.reviewCount = reviewCount; return this; }
        public Builder stock(Integer stock) { this.stock = stock; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder spec(String spec) { this.spec = spec; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder flashSaleEndTime(LocalDateTime flashSaleEndTime) { this.flashSaleEndTime = flashSaleEndTime; return this; }
        public Builder enabled(Integer enabled) { this.enabled = enabled; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Product build() {
            return new Product(id, name, description, image, price, originalPrice, discount, sales, rating, reviewCount, stock, brand, spec, categoryId, flashSaleEndTime, enabled, createdAt, updatedAt);
        }
    }
}