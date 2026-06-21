package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.md.basePlatform.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品数据访问接口
 */
@Mapper
public interface ProductRepository extends BaseMapper<Product> {

    /**
     * 分页查询商品列表
     */
    IPage<Product> selectProducts(
            Page<Product> page,
            @Param("categoryId") Long categoryId
    );
}