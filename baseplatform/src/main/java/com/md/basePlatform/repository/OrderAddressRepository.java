package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.md.basePlatform.domain.OrderAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单收货地址数据访问接口
 */
@Mapper
public interface OrderAddressRepository extends BaseMapper<OrderAddress> {
}