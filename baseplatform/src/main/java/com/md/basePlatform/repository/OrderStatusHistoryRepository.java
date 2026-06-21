package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.md.basePlatform.domain.OrderStatusHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单状态历史数据访问接口
 */
@Mapper
public interface OrderStatusHistoryRepository extends BaseMapper<OrderStatusHistory> {
}