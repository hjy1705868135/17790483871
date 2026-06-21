package com.md.basePlatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.exception.OrderException;
import com.md.basePlatform.repository.OrderRepository;
import com.md.basePlatform.repository.ProductRepository;
import com.md.basePlatform.repository.ReviewRepository;
import com.md.basePlatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价服务
 */
@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository,
                        ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * 提交评价
     */
    @Transactional
    public ReviewResponse submitReview(Long userId, ReviewRequest request) {
        log.info("Submit review for user: {}, product: {}", userId, request.getProductId());

        // 验证订单是否属于该用户
        Order order = orderRepository.selectById(request.getOrderId());
        if (order == null || order.getDeleted() == 1 || !order.getUserId().equals(userId)) {
            throw new OrderException("订单不存在或无权评价");
        }

        // 验证订单是否已完成
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new OrderException("只能评价已完成的订单");
        }

        // 验证是否已评价
        Review existingReview = reviewRepository.selectOne(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getOrderItemId, request.getOrderItemId())
                        .eq(Review::getUserId, userId)
                        .eq(Review::getDeleted, 0)
        );

        if (existingReview != null) {
            throw new OrderException("该商品已评价");
        }

        // 创建评价
        Review review = new Review();
        review.setOrderId(request.getOrderId());
        review.setOrderItemId(request.getOrderItemId());
        review.setProductId(request.getProductId());
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setDeleted(0);

        reviewRepository.insert(review);

        // 更新商品评分
        updateProductRating(request.getProductId());

        log.info("Review submitted successfully: {}", review.getId());

        return buildReviewResponse(review);
    }

    /**
     * 获取商品评价列表
     */
    public IPage<ReviewResponse> getProductReviews(Long productId, int page, int size) {
        Page<Review> pageParam = new Page<>(page, size);
        IPage<Review> reviewPage = reviewRepository.selectProductReviews(pageParam, productId);
        return reviewPage.convert(this::buildReviewResponse);
    }

    /**
     * 获取用户评价列表
     */
    public IPage<ReviewResponse> getUserReviews(Long userId, int page, int size) {
        Page<Review> pageParam = new Page<>(page, size);
        IPage<Review> reviewPage = reviewRepository.selectUserReviews(pageParam, userId);
        return reviewPage.convert(this::buildReviewResponse);
    }

    /**
     * 回复评价（管理员）
     */
    @Transactional
    public ReviewResponse replyReview(Long reviewId, String reply) {
        Review review = reviewRepository.selectById(reviewId);
        if (review == null || review.getDeleted() == 1) {
            throw new OrderException("评价不存在");
        }

        review.setReply(reply);
        review.setReplyTime(LocalDateTime.now());
        reviewRepository.updateById(review);

        log.info("Review replied: {}", reviewId);

        return buildReviewResponse(review);
    }

    /**
     * 获取订单可评价商品
     */
    public List<ReviewableItemResponse> getReviewableItems(Long userId, Long orderId) {
        // 验证订单
        Order order = orderRepository.selectById(orderId);
        if (order == null || order.getDeleted() == 1 || !order.getUserId().equals(userId)) {
            throw new OrderException("订单不存在");
        }

        if (!"COMPLETED".equals(order.getStatus())) {
            throw new OrderException("只能评价已完成的订单");
        }

        // 获取订单商品
        List<OrderItem> orderItems = orderRepository.selectOrderItems(orderId);

        // 过滤出未评价的商品
        return orderItems.stream()
                .filter(item -> {
                    Review review = reviewRepository.selectOne(
                            new LambdaQueryWrapper<Review>()
                                    .eq(Review::getOrderItemId, item.getId())
                                    .eq(Review::getDeleted, 0)
                    );
                    return review == null;
                })
                .map(item -> {
                    Product product = productRepository.selectById(item.getProductId());
                    ReviewableItemResponse resp = new ReviewableItemResponse();
                    resp.setOrderItemId(item.getId());
                    resp.setProductId(item.getProductId());
                    resp.setProductName(item.getProductName());
                    resp.setProductImage(item.getProductImage());
                    resp.setPrice(item.getPrice().doubleValue());
                    return resp;
                })
                .toList();
    }

    private void updateProductRating(Long productId) {
        List<Review> reviews = reviewRepository.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getProductId, productId)
                        .eq(Review::getDeleted, 0)
        );

        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0);

            Product product = productRepository.selectById(productId);
            if (product != null) {
                product.setRating(avgRating);
                product.setReviewCount(reviews.size());
                productRepository.updateById(product);
            }
        }
    }

    private ReviewResponse buildReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setOrderId(review.getOrderId());
        response.setOrderItemId(review.getOrderItemId());
        response.setProductId(review.getProductId());
        response.setUserId(review.getUserId());
        response.setRating(review.getRating());
        response.setContent(review.getContent());
        response.setImages(review.getImages());
        response.setReply(review.getReply());
        response.setReplyTime(review.getReplyTime() != null ? review.getReplyTime().toString() : null);
        response.setCreatedAt(review.getCreatedAt() != null ? review.getCreatedAt().toString() : null);

        // 获取用户信息
        User user = userRepository.selectById(review.getUserId());
        if (user != null) {
            response.setUsername(user.getUsername());
            response.setNickname(user.getNickname());
        }

        return response;
    }
}
