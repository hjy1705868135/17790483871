/**
 * 订单控制器
 * 处理订单查询、创建等订单相关请求
 */
const { Order, OrderStatus, OrderStatusText } = require('../models/Order');
const { successResponse, errorResponse, paginatedResponse } = require('../utils/response');
const logger = require('../utils/logger');
const config = require('../config');

/**
 * 获取订单列表（支持分页、筛选、排序）
 * @route GET /api/orders
 * @query {number} page - 页码
 * @query {number} pageSize - 每页数量
 * @query {string} status - 订单状态筛选
 * @query {string} userId - 用户ID筛选
 * @query {string} userName - 用户名筛选（模糊匹配）
 * @query {string} orderNo - 订单号筛选（模糊匹配）
 * @query {string} startDate - 开始日期
 * @query {string} endDate - 结束日期
 * @query {number} minAmount - 最小金额
 * @query {number} maxAmount - 最大金额
 * @query {string} sortBy - 排序字段
 * @query {string} sortOrder - 排序方向
 */
const getOrders = async (req, res) => {
  try {
    // 解析查询参数
    const {
      page = config.pagination.defaultPage,
      pageSize = config.pagination.defaultPageSize,
      status,
      userId,
      userName,
      orderNo,
      startDate,
      endDate,
      minAmount,
      maxAmount,
      sortBy = 'createdAt',
      sortOrder = 'desc',
    } = req.query;

    logger.info('订单列表查询请求', {
      userId: req.user?.userId,
      params: req.query,
    });

    // 构建查询选项
    const options = {
      page: parseInt(page, 10),
      pageSize: Math.min(parseInt(pageSize, 10), config.pagination.maxPageSize),
      status,
      userId,
      userName,
      orderNo,
      startDate,
      endDate,
      minAmount: minAmount !== undefined ? parseFloat(minAmount) : undefined,
      maxAmount: maxAmount !== undefined ? parseFloat(maxAmount) : undefined,
      sortBy,
      sortOrder,
    };

    // 执行查询
    const result = await Order.findAll(options);

    // 添加状态文本
    const itemsWithStatusText = result.items.map((order) => ({
      ...order,
      statusText: OrderStatusText[order.status] || order.status,
    }));

    logger.info('订单列表查询成功', {
      total: result.total,
      page: result.page,
      pageSize: result.pageSize,
    });

    return paginatedResponse(
      res,
      itemsWithStatusText,
      result.total,
      result.page,
      result.pageSize,
      '查询成功'
    );
  } catch (error) {
    logger.error('订单列表查询失败', {
      error: error.message,
      stack: error.stack,
    });
    return errorResponse(
      res,
      '查询订单列表失败',
      500,
      'QUERY_ORDERS_ERROR'
    );
  }
};

/**
 * 获取订单详情
 * @route GET /api/orders/:id
 */
const getOrderById = async (req, res) => {
  try {
    const { id } = req.params;

    logger.info('订单详情查询请求', { orderId: id, userId: req.user?.userId });

    const order = await Order.findById(id);

    if (!order) {
      logger.warn('订单不存在', { orderId: id });
      return errorResponse(res, '订单不存在', 404, 'ORDER_NOT_FOUND');
    }

    // 添加状态文本
    const orderWithStatusText = {
      ...order,
      statusText: OrderStatusText[order.status] || order.status,
    };

    return successResponse(res, { order: orderWithStatusText }, '查询成功');
  } catch (error) {
    logger.error('订单详情查询失败', {
      error: error.message,
      orderId: req.params.id,
    });
    return errorResponse(
      res,
      '查询订单详情失败',
      500,
      'QUERY_ORDER_ERROR'
    );
  }
};

/**
 * 获取订单状态列表
 * @route GET /api/orders/statuses
 */
const getOrderStatuses = async (req, res) => {
  try {
    const statuses = Object.values(OrderStatus).map((status) => ({
      value: status,
      label: OrderStatusText[status],
    }));

    return successResponse(res, { statuses }, '查询成功');
  } catch (error) {
    logger.error('获取订单状态列表失败', { error: error.message });
    return errorResponse(
      res,
      '获取订单状态列表失败',
      500,
      'GET_STATUSES_ERROR'
    );
  }
};

/**
 * 创建订单
 * @route POST /api/orders
 */
const createOrder = async (req, res) => {
  try {
    const { items, shippingAddress, userId, userName } = req.body;

    logger.info('创建订单请求', { userId: req.user?.userId });

    // 计算总金额
    const totalAmount = items.reduce((sum, item) => {
      return sum + item.price * item.quantity;
    }, 0);

    const orderData = {
      userId: userId || req.user?.userId,
      userName: userName || req.user?.username,
      items,
      totalAmount,
      shippingAddress,
    };

    const order = await Order.create(orderData);

    logger.info('订单创建成功', { orderId: order.id, orderNo: order.orderNo });

    return successResponse(
      res,
      {
        order: {
          ...order,
          statusText: OrderStatusText[order.status],
        },
      },
      '订单创建成功',
      201
    );
  } catch (error) {
    logger.error('创建订单失败', { error: error.message, stack: error.stack });
    return errorResponse(res, '创建订单失败', 500, 'CREATE_ORDER_ERROR');
  }
};

/**
 * 更新订单状态
 * @route PUT /api/orders/:id/status
 */
const updateOrderStatus = async (req, res) => {
  try {
    const { id } = req.params;
    const { status } = req.body;

    logger.info('更新订单状态请求', {
      orderId: id,
      newStatus: status,
      userId: req.user?.userId,
    });

    // 验证状态值
    if (!Object.values(OrderStatus).includes(status)) {
      return errorResponse(res, '无效的订单状态', 400, 'INVALID_STATUS');
    }

    const order = await Order.updateStatus(id, status);

    if (!order) {
      return errorResponse(res, '订单不存在', 404, 'ORDER_NOT_FOUND');
    }

    return successResponse(
      res,
      {
        order: {
          ...order,
          statusText: OrderStatusText[order.status],
        },
      },
      '订单状态更新成功'
    );
  } catch (error) {
    logger.error('更新订单状态失败', {
      error: error.message,
      orderId: req.params.id,
    });
    return errorResponse(
      res,
      '更新订单状态失败',
      500,
      'UPDATE_STATUS_ERROR'
    );
  }
};

module.exports = {
  getOrders,
  getOrderById,
  getOrderStatuses,
  createOrder,
  updateOrderStatus,
};