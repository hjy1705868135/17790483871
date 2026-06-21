package com.md.basePlatform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.md.basePlatform.common.ApiResponse;
import com.md.basePlatform.domain.Category;
import com.md.basePlatform.domain.ProductDetailResponse;
import com.md.basePlatform.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 分页查询商品列表
     */
    @GetMapping
    public ApiResponse<IPage<ProductDetailResponse>> getProducts(
            @RequestParam(value = "page", defaultValue = "1") int pageNum,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "sortBy", defaultValue = "default") String sortBy,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice) {
        
        IPage<ProductDetailResponse> products = productService.getProducts(
                categoryId, sortBy, minPrice, maxPrice, pageNum, pageSize
        );
        return ApiResponse.success(products);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable(value = "id") Long id) {
        ProductDetailResponse product = productService.getProductDetail(id);
        return ApiResponse.success(product);
    }

    /**
     * 获取分类列表
     */
    @GetMapping("/categories")
    public ApiResponse<List<Category>> getCategories() {
        List<Category> categories = productService.getCategories();
        return ApiResponse.success(categories);
    }
}