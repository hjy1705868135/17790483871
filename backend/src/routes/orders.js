/**
 * 订单路由
 */
const express = require('express');
const router = express.Router();
const orderController = require('../controllers/orderController');
const { authMiddleware } = require('../middleware/auth');
const { orderQueryValidationRules } = require('../middleware/validate');
const { body, validationResult } = require('express-validator');
const { errorResponse } = require('../utils/response');

/**
 * @route GET /api/orders/statuses
 * @desc 获取订单状态列表
 * @access Private
 */
router.get('/statuses', authMiddleware, orderController.getOrderStatuses);

/**
 * @route GET /api/orders
 * @desc 获取订单列表（支持分页、筛选、排序）
 * @access Private
 */
router.get('/', authMiddleware, orderQueryValidationRules, orderController.getOrders);

/**
 * @route GET /api/orders/:id
 * @desc 获取订单详情
 * @access Private
 */
router.get('/:id', authMiddleware, orderController.getOrderById);

/**
 * @route POST /api/orders
 * @desc 创建订单
 * @access Private
 */
router.post(
  '/',
  authMiddleware,
  [
    body('items').isArray({ min: 1 }).withMessage('订单项不能为空'),
    body('items.*.productId').notEmpty().withMessage('商品ID不能为空'),
    body('items.*.productName').notEmpty().withMessage('商品名称不能为空'),
    body('items.*.quantity').isInt({ min: 1 }).withMessage('商品数量必须大于0'),
    body('items.*.price').isFloat({ min: 0 }).withMessage('商品价格必须大于等于0'),
    body('shippingAddress').notEmpty().withMessage('收货地址不能为空'),
    (req, res, next) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        const errorMessages = errors.array().map((error) => ({
          field: error.path,
          message: error.msg,
        }));
        return errorResponse(res, '请求参数验证失败', 400, 'VALIDATION_ERROR', errorMessages);
      }
      next();
    },
  ],
  orderController.createOrder
);

/**
 * @route PUT /api/orders/:id/status
 * @desc 更新订单状态
 * @access Private
 */
router.put(
  '/:id/status',
  authMiddleware,
  [
    body('status')
      .isIn(['pending', 'paid', 'shipped', 'delivered', 'cancelled'])
      .withMessage('无效的订单状态'),
    (req, res, next) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        const errorMessages = errors.array().map((error) => ({
          field: error.path,
          message: error.msg,
        }));
        return errorResponse(res, '请求参数验证失败', 400, 'VALIDATION_ERROR', errorMessages);
      }
      next();
    },
  ],
  orderController.updateOrderStatus
);

module.exports = router;