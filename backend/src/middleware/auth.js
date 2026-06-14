/**
 * 认证中间件
 * 验证JWT Token并注入用户信息到请求对象
 */
const { verifyToken } = require('../utils/jwt');
const { errorResponse } = require('../utils/response');
const logger = require('../utils/logger');

/**
 * 认证中间件
 * 从请求头中提取并验证JWT Token
 */
const authMiddleware = async (req, res, next) => {
  try {
    // 从请求头获取Token
    const authHeader = req.headers.authorization;

    if (!authHeader) {
      logger.warn('认证失败：缺少Authorization头');
      return errorResponse(
        res,
        '未提供认证令牌',
        401,
        'MISSING_TOKEN'
      );
    }

    // 检查Bearer格式
    const parts = authHeader.split(' ');
    if (parts.length !== 2 || parts[0] !== 'Bearer') {
      logger.warn('认证失败：Token格式错误', { authHeader });
      return errorResponse(
        res,
        '认证令牌格式错误，请使用Bearer Token格式',
        401,
        'INVALID_TOKEN_FORMAT'
      );
    }

    const token = parts[1];

    // 验证Token
    const decoded = verifyToken(token);

    if (!decoded) {
      logger.warn('认证失败：Token无效或已过期');
      return errorResponse(
        res,
        '认证令牌无效或已过期，请重新登录',
        401,
        'INVALID_TOKEN'
      );
    }

    // 将用户信息注入请求对象
    req.user = {
      userId: decoded.userId,
      username: decoded.username,
      role: decoded.role,
    };

    logger.debug('认证成功', { userId: decoded.userId });
    next();
  } catch (error) {
    logger.error('认证中间件异常', { error: error.message });
    return errorResponse(
      res,
      '认证过程发生错误',
      500,
      'AUTH_ERROR'
    );
  }
};

/**
 * 可选认证中间件
 * 如果提供了Token则验证，否则继续执行
 */
const optionalAuthMiddleware = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;

    if (authHeader) {
      const parts = authHeader.split(' ');
      if (parts.length === 2 && parts[0] === 'Bearer') {
        const token = parts[1];
        const decoded = verifyToken(token);

        if (decoded) {
          req.user = {
            userId: decoded.userId,
            username: decoded.username,
            role: decoded.role,
          };
        }
      }
    }

    next();
  } catch (error) {
    logger.error('可选认证中间件异常', { error: error.message });
    next();
  }
};

/**
 * 角色权限中间件
 * @param {Array} allowedRoles - 允许的角色列表
 */
const roleMiddleware = (allowedRoles) => {
  return (req, res, next) => {
    if (!req.user) {
      return errorResponse(
        res,
        '未认证用户',
        401,
        'UNAUTHORIZED'
      );
    }

    if (!allowedRoles.includes(req.user.role)) {
      logger.warn('权限不足', {
        userId: req.user.userId,
        userRole: req.user.role,
        requiredRoles: allowedRoles,
      });
      return errorResponse(
        res,
        '权限不足，无法访问此资源',
        403,
        'FORBIDDEN'
      );
    }

    next();
  };
};

module.exports = {
  authMiddleware,
  optionalAuthMiddleware,
  roleMiddleware,
};