package com.example.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.order.dto.OrderCreateDTO;
import com.example.order.dto.OrderQueryDTO;
import com.example.order.dto.OrderUpdateDTO;
import com.example.order.entity.Order;
import com.example.order.vo.OrderVO;

/**
 * 订单服务接口
 *
 * @author API Team
 * @version 1.0
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     *
     * @param dto 订单创建信息
     * @return 订单详情
     */
    OrderVO createOrder(OrderCreateDTO dto);

    /**
     * 更新订单信息
     *
     * @param id  订单ID
     * @param dto 订单更新信息
     * @return 订单详情
     */
    OrderVO updateOrder(Long id, OrderUpdateDTO dto);

    /**
     * 根据ID查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    OrderVO getOrderById(Long id);

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo 订单编号
     * @return 订单详情
     */
    OrderVO getOrderByOrderNo(String orderNo);

    /**
     * 分页查询订单列表
     *
     * @param query 查询条件
     * @return 订单分页列表
     */
    IPage<OrderVO> queryOrderPage(OrderQueryDTO query);

    /**
     * 删除订单（逻辑删除）
     *
     * @param id 订单ID
     * @return 是否删除成功
     */
    boolean deleteOrder(Long id);

    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateOrderStatus(Long id, String status);

    /**
     * 支付订单
     *
     * @param id             订单ID
     * @param paymentMethod  支付方式
     * @param paymentNo      支付流水号
     * @return 是否支付成功
     */
    boolean payOrder(Long id, String paymentMethod, String paymentNo);

    /**
     * 发货
     *
     * @param id              订单ID
     * @param expressCompany  快递公司
     * @param expressNo       快递单号
     * @return 是否发货成功
     */
    boolean shipOrder(Long id, String expressCompany, String expressNo);

    /**
     * 确认收货
     *
     * @param id 订单ID
     * @return 是否确认成功
     */
    boolean confirmDelivery(Long id);

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 是否取消成功
     */
    boolean cancelOrder(Long id);
}
