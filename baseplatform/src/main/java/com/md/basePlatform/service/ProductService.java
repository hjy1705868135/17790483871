package com.md.basePlatform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.md.basePlatform.domain.Category;
import com.md.basePlatform.domain.Product;
import com.md.basePlatform.domain.ProductDetailResponse;
import com.md.basePlatform.exception.ProductNotFoundException;
import com.md.basePlatform.repository.CategoryRepository;
import com.md.basePlatform.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商品服务
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 分页查询商品列表
     */
    public IPage<ProductDetailResponse> getProducts(Long categoryId, String sortBy,
                                                    BigDecimal minPrice, BigDecimal maxPrice,
                                                    int page, int size) {
        Page<Product> pageParam = new Page<>(page, size);
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getEnabled, 1);
        
        if (categoryId != null) {
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }
        
        if (minPrice != null) {
            queryWrapper.ge(Product::getPrice, minPrice);
        }
        
        if (maxPrice != null) {
            queryWrapper.le(Product::getPrice, maxPrice);
        }
        
        // 根据排序字段排序
        if ("price_asc".equals(sortBy)) {
            queryWrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            queryWrapper.orderByDesc(Product::getPrice);
        } else if ("sales".equals(sortBy)) {
            queryWrapper.orderByDesc(Product::getSales);
        } else if ("rating".equals(sortBy)) {
            queryWrapper.orderByDesc(Product::getRating);
        } else {
            queryWrapper.orderByDesc(Product::getId);
        }
        
        IPage<Product> productPage = productRepository.selectPage(pageParam, queryWrapper);

        return productPage.convert(this::buildProductResponse);
    }

    /**
     * 获取商品详情
     */
    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = productRepository.selectById(productId);

        if (product == null || product.getEnabled() == 0) {
            throw new ProductNotFoundException("商品不存在或已下架");
        }

        Category category = categoryRepository.selectById(product.getCategoryId());

        return buildProductDetailResponse(product, category);
    }

    /**
     * 获取分类列表
     */
    public List<Category> getCategories() {
        return categoryRepository.selectList(null);
    }

    /**
     * 构建商品响应
     */
    private ProductDetailResponse buildProductResponse(Product product) {
        List<ProductDetailResponse.PromotionDTO> promotions = buildPromotions(product);

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .image(product.getImage())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .discount(product.getDiscount())
                .sales(product.getSales())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .stock(product.getStock())
                .brand(product.getBrand())
                .spec(product.getSpec())
                .categoryId(product.getCategoryId())
                .flashSaleEndTime(product.getFlashSaleEndTime() != null ? 
                        product.getFlashSaleEndTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .promotions(promotions)
                .build();
    }

    /**
     * 构建商品详情响应
     */
    private ProductDetailResponse buildProductDetailResponse(Product product, Category category) {
        List<ProductDetailResponse.PromotionDTO> promotions = buildPromotions(product);

        Map<String, String> params = new HashMap<>();
        params.put("品牌", product.getBrand());
        params.put("规格", product.getSpec());
        params.put("库存", String.valueOf(product.getStock()));
        params.put("销量", String.valueOf(product.getSales()));

        List<String> images = new ArrayList<>();
        images.add(product.getImage());
        images.add(product.getImage());
        images.add(product.getImage());

        List<String> detailImages = new ArrayList<>();
        detailImages.add(product.getImage());
        detailImages.add(product.getImage());

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .image(product.getImage())
                .images(images)
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .discount(product.getDiscount())
                .sales(product.getSales())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .stock(product.getStock())
                .brand(product.getBrand())
                .spec(product.getSpec())
                .categoryId(product.getCategoryId())
                .categoryName(category != null ? category.getName() : "")
                .flashSaleEndTime(product.getFlashSaleEndTime() != null ? 
                        product.getFlashSaleEndTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .promotions(promotions)
                .params(params)
                .detailImages(detailImages)
                .build();
    }

    /**
     * 构建促销标签列表
     */
    private List<ProductDetailResponse.PromotionDTO> buildPromotions(Product product) {
        List<ProductDetailResponse.PromotionDTO> promotions = new ArrayList<>();

        if (product.getDiscount() != null && product.getDiscount() > 0) {
            promotions.add(ProductDetailResponse.PromotionDTO.builder()
                    .type("sale")
                    .text("限时折扣")
                    .discount(product.getDiscount())
                    .build());
        }

        if (product.getFlashSaleEndTime() != null && 
                product.getFlashSaleEndTime().isAfter(java.time.LocalDateTime.now())) {
            promotions.add(ProductDetailResponse.PromotionDTO.builder()
                    .type("flash")
                    .text("闪购")
                    .build());
        }

        if (product.getSales() != null && product.getSales() > 1000) {
            promotions.add(ProductDetailResponse.PromotionDTO.builder()
                    .type("hot")
                    .text("热销")
                    .build());
        }

        return promotions;
    }
}