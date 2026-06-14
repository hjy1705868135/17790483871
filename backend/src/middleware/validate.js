/**
 * 请求验证中间件
 * 使用express-validator进行请求参数验证
 */
const { validationResult, body, query } = require('express-validator');
const { errorResponse } = require('../utils/response');
const logger = require('../utils/logger');

/**
 * 验证结果处理中间件
 */
const validate = (req, res, next) => {
  const errors = validationResult(req);

  if (!errors.isEmpty()) {
    const errorMessages = errors.array().map((error) => ({
      field: error.path,
      message: error.msg,
      value: error.value,
    }));

    logger.warn('请求参数验证失败', { errors: errorMessages });

    return errorResponse(
      res,
      '请求参数验证失败',
      400,
      'VALIDATION_ERROR',
      errorMessages
    );
  }

  next();
};

/**
 * 登录请求验证规则
 */
const loginValidationRules = [
  body('username')
    .trim()
    .notEmpty()
    .withMessage('用户名不能为空')
    .isLength({ min: 3, max: 50 })
    .withMessage('用户名长度必须在3-50个字符之间'),

  body('password')
    .notEmpty()
    .withMessage('密码不能为空')
    .isLength({ min: 6, max: 100 })
    .withMessage('密码长度必须在6-100个字符之间'),

  validate,
];

/**
 * 订单查询请求验证规则
 */
const orderQueryValidationRules = [
  query('page')
    .optional()
    .isInt({ min: 1 })
    .withMessage('页码必须是大于0的整数'),

  query('pageSize')
    .optional()
    .isInt({ min: 1, max: 100 })
    .withMessage('每页数量必须是1-100之间的整数'),

  query('status')
    .optional()
    .isIn(['pending', 'paid', 'shipped', 'delivered', 'cancelled'])
    .withMessage('订单状态值无效'),

  query('minAmount')
    .optional()
    .isFloat({ min: 0 })
    .withMessage('最小金额必须是非负数'),

  query('maxAmount')
    .optional()
    .isFloat({ min: 0 })
    .withMessage('最大金额必须是非负数')
    .custom((value, { req }) => {
      if (req.query.minAmount && parseFloat(value) < parseFloat(req.query.minAmount)) {
        throw new Error('最大金额不能小于最小金额');
      }
      return true;
    }),

  query('sortBy')
    .optional()
    .isIn(['createdAt', 'updatedAt', 'totalAmount', 'orderNo', 'status'])
    .withMessage('排序字段无效'),

  query('sortOrder')
    .optional()
    .isIn(['asc', 'desc'])
    .withMessage('排序方向必须是asc或desc'),

  query('startDate')
    .optional()
    .isISO8601()
    .withMessage('开始日期格式无效，请使用ISO8601格式'),

  query('endDate')
    .optional()
    .isISO8601()
    .withMessage('结束日期格式无效，请使用ISO8601格式')
    .custom((value, { req }) => {
      if (req.query.startDate && new Date(value) < new Date(req.query.startDate)) {
        throw new Error('结束日期不能早于开始日期');
      }
      return true;
    }),

  validate,
];

/**
 * 错误处理中间件
 */
const errorHandler = (err, req, res, next) => {
  logger.error('服务器错误', {
    error: err.message,
    stack: err.stack,
    path: req.path,
    method: req.method,
  });

  // 处理特定错误类型
  if (err.name === 'UnauthorizedError') {
    return errorResponse(res, '认证失败', 401, 'UNAUTHORIZED');
  }

  if (err.name === 'SyntaxError' && err.status === 400 && 'body' in err) {
    return errorResponse(res, 'JSON格式错误', 400, 'INVALID_JSON');
  }

  return errorResponse(
    res,
    process.env.NODE_ENV === 'production' ? '服务器内部错误' : err.message,
    500,
    'INTERNAL_ERROR'
  );
};

/**
 * 404处理中间件
 */
const notFoundHandler = (req, res) => {
  logger.warn('资源未找到', { path: req.path, method: req.method });
  return errorResponse(res, '请求的资源不存在', 404, 'NOT_FOUND');
};

module.exports = {
  validate,
  loginValidationRules,
  orderQueryValidationRules,
  errorHandler,
  notFoundHandler,
};