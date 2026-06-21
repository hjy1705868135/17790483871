package com.md.basePlatform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.md.basePlatform.common.ApiResponse;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.service.ReviewService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * 提交评价
     */
    @PostMapping
    public ApiResponse<ReviewResponse> submitReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ReviewResponse review = reviewService.submitReview(userId, request);
        return ApiResponse.success("评价提交成功", review);
    }

    /**
     * 获取商品评价列表
     */
    @GetMapping("/product/{productId}")
    public ApiResponse<IPage<ReviewResponse>> getProductReviews(
            @PathVariable(value = "productId") Long productId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        IPage<ReviewResponse> reviews = reviewService.getProductReviews(productId, page, size);
        return ApiResponse.success(reviews);
    }

    /**
     * 获取用户评价列表
     */
    @GetMapping("/my")
    public ApiResponse<IPage<ReviewResponse>> getMyReviews(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        IPage<ReviewResponse> reviews = reviewService.getUserReviews(userId, page, size);
        return ApiResponse.success(reviews);
    }

    /**
     * 获取订单可评价商品
     */
    @GetMapping("/order/{orderId}/items")
    public ApiResponse<List<ReviewableItemResponse>> getReviewableItems(
            @PathVariable(value = "orderId") Long orderId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<ReviewableItemResponse> items = reviewService.getReviewableItems(userId, orderId);
        return ApiResponse.success(items);
    }

    /**
     * 回复评价（管理员）
     */
    @PostMapping("/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReviewResponse> replyReview(
            @PathVariable(value = "id") Long id,
            @RequestBody Map<String, String> replyData,
            Authentication authentication) {
        String reply = replyData.get("reply");
        ReviewResponse review = reviewService.replyReview(id, reply);
        return ApiResponse.success("回复成功", review);
    }
}
