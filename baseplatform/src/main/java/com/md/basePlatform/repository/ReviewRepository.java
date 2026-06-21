package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.md.basePlatform.domain.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 评价仓库
 */
@Mapper
public interface ReviewRepository extends BaseMapper<Review> {
    
    /**
     * 分页查询商品评价
     */
    IPage<Review> selectProductReviews(Page<Review> page, @Param("productId") Long productId);
    
    /**
     * 分页查询用户评价
     */
    IPage<Review> selectUserReviews(Page<Review> page, @Param("userId") Long userId);
}
