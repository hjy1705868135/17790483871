package com.example.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.order.dto.OrderQueryDTO;
import com.example.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单Mapper接口
 *
 * @author API Team
 * @version 1.0
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 分页查询订单列表
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 订单分页列表
     */
    IPage<Order> selectOrderPage(Page<Order> page, @Param("query") OrderQueryDTO query);

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);
}
