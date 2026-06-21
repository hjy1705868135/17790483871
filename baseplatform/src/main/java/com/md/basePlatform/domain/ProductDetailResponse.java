package com.md.basePlatform.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品详情响应DTO
 */
public class ProductDetailResponse {

    /**
     * 商品ID
     */
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
     * 图片列表
     */
    private List<String> images;

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
     * 分类名称
     */
    private String categoryName;

    /**
     * 闪购结束时间戳
     */
    private Long flashSaleEndTime;

    /**
     * 促销标签列表
     */
    private List<PromotionDTO> promotions;

    /**
     * 商品参数
     */
    private Map<String, String> params;

    /**
     * 详情图片
     */
    private List<String> detailImages;

    public ProductDetailResponse() {}

    public ProductDetailResponse(Long id, String name, String description, String image, List<String> images,
                                BigDecimal price, BigDecimal originalPrice, Integer discount, Integer sales,
                                Double rating, Integer reviewCount, Integer stock, String brand, String spec,
                                Long categoryId, String categoryName, Long flashSaleEndTime,
                                List<PromotionDTO> promotions, Map<String, String> params, List<String> detailImages) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.images = images;
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
        this.categoryName = categoryName;
        this.flashSaleEndTime = flashSaleEndTime;
        this.promotions = promotions;
        this.params = params;
        this.detailImages = detailImages;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
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
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Long getFlashSaleEndTime() { return flashSaleEndTime; }
    public void setFlashSaleEndTime(Long flashSaleEndTime) { this.flashSaleEndTime = flashSaleEndTime; }
    public List<PromotionDTO> getPromotions() { return promotions; }
    public void setPromotions(List<PromotionDTO> promotions) { this.promotions = promotions; }
    public Map<String, String> getParams() { return params; }
    public void setParams(Map<String, String> params) { this.params = params; }
    public List<String> getDetailImages() { return detailImages; }
    public void setDetailImages(List<String> detailImages) { this.detailImages = detailImages; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String image;
        private List<String> images;
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
        private String categoryName;
        private Long flashSaleEndTime;
        private List<PromotionDTO> promotions;
        private Map<String, String> params;
        private List<String> detailImages;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder image(String image) { this.image = image; return this; }
        public Builder images(List<String> images) { this.images = images; return this; }
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
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder flashSaleEndTime(Long flashSaleEndTime) { this.flashSaleEndTime = flashSaleEndTime; return this; }
        public Builder promotions(List<PromotionDTO> promotions) { this.promotions = promotions; return this; }
        public Builder params(Map<String, String> params) { this.params = params; return this; }
        public Builder detailImages(List<String> detailImages) { this.detailImages = detailImages; return this; }

        public ProductDetailResponse build() {
            return new ProductDetailResponse(id, name, description, image, images, price, originalPrice, discount,
                    sales, rating, reviewCount, stock, brand, spec, categoryId, categoryName, flashSaleEndTime,
                    promotions, params, detailImages);
        }
    }

    /**
     * 促销DTO
     */
    public static class PromotionDTO {
        private String type;
        private String text;
        private Integer discount;

        public PromotionDTO() {}

        public PromotionDTO(String type, String text, Integer discount) {
            this.type = type;
            this.text = text;
            this.discount = discount;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public Integer getDiscount() { return discount; }
        public void setDiscount(Integer discount) { this.discount = discount; }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String type;
            private String text;
            private Integer discount;

            public Builder type(String type) { this.type = type; return this; }
            public Builder text(String text) { this.text = text; return this; }
            public Builder discount(Integer discount) { this.discount = discount; return this; }

            public PromotionDTO build() {
                return new PromotionDTO(type, text, discount);
            }
        }
    }
}