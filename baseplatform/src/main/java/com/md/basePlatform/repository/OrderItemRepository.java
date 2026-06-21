package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.md.basePlatform.domain.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单商品数据访问接口
 */
@Mapper
public interface OrderItemRepository extends BaseMapper<OrderItem> {
}