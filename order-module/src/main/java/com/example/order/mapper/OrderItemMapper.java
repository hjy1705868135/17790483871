package com.example.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单详情Mapper接口
 *
 * @author API Team
 * @version 1.0
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据订单ID查询订单详情列表
     *
     * @param orderId 订单ID
     * @return 订单详情列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单编号查询订单详情列表
     *
     * @param orderNo 订单编号
     * @return 订单详情列表
     */
    List<OrderItem> selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 批量插入订单详情
     *
     * @param items 订单详情列表
     * @return 影响行数
     */
    int batchInsert(@Param("items") List<OrderItem> items);

    /**
     * 根据订单ID删除订单详情（逻辑删除）
     *
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteByOrderId(@Param("orderId") Long orderId);
}
