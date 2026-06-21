package com.md.basePlatform.controller;

import com.md.basePlatform.common.ApiResponse;
import com.md.basePlatform.domain.Category;
import com.md.basePlatform.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final ProductService productService;

    public CategoryController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 获取分类列表
     */
    @GetMapping
    public ApiResponse<List<Category>> getCategories() {
        List<Category> categories = productService.getCategories();
        return ApiResponse.success(categories);
    }
}