/**
 * API响应工具模块
 * 提供统一的API响应格式
 */
const logger = require('./logger');

/**
 * 成功响应
 * @param {Object} res - Express响应对象
 * @param {*} data - 响应数据
 * @param {string} message - 成功消息
 * @param {number} statusCode - HTTP状态码
 */
const successResponse = (res, data = null, message = '操作成功', statusCode = 200) => {
  const response = {
    success: true,
    message,
    timestamp: new Date().toISOString(),
    data,
  };
  
  logger.debug('API成功响应', { statusCode, message });
  return res.status(statusCode).json(response);
};

/**
 * 错误响应
 * @param {Object} res - Express响应对象
 * @param {string} message - 错误消息
 * @param {number} statusCode - HTTP状态码
 * @param {string} errorCode - 错误代码
 * @param {*} errors - 详细错误信息
 */
const errorResponse = (res, message = '操作失败', statusCode = 500, errorCode = 'INTERNAL_ERROR', errors = null) => {
  const response = {
    success: false,
    message,
    errorCode,
    timestamp: new Date().toISOString(),
    ...(errors && { errors }),
  };
  
  logger.error('API错误响应', { statusCode, errorCode, message });
  return res.status(statusCode).json(response);
};

/**
 * 分页响应
 * @param {Object} res - Express响应对象
 * @param {Array} items - 数据项列表
 * @param {number} total - 总记录数
 * @param {number} page - 当前页码
 * @param {number} pageSize - 每页大小
 * @param {string} message - 成功消息
 */
const paginatedResponse = (res, items, total, page, pageSize, message = '查询成功') => {
  const totalPages = Math.ceil(total / pageSize);
  
  const data = {
    items,
    pagination: {
      page,
      pageSize,
      total,
      totalPages,
      hasNext: page < totalPages,
      hasPrev: page > 1,
    },
  };
  
  return successResponse(res, data, message);
};

module.exports = {
  successResponse,
  errorResponse,
  paginatedResponse,
};