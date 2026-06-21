package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.md.basePlatform.domain.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类数据访问接口
 */
@Mapper
public interface CategoryRepository extends BaseMapper<Category> {
}