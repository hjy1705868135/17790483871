package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.md.basePlatform.domain.Order;
import com.md.basePlatform.domain.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 订单数据访问接口
 */
@Mapper
public interface OrderRepository extends BaseMapper<Order> {

    /**
     * 分页查询用户的订单
     */
    IPage<Order> selectUserOrders(Page<Order> page, @Param("userId") Long userId, @Param("status") String status);

    /**
     * 查询订单的所有商品
     */
    List<OrderItem> selectOrderItems(@Param("orderId") Long orderId);
}