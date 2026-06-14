/**
 * 认证控制器
 * 处理用户登录、登出等认证相关请求
 */
const { User } = require('../models/User');
const { generateToken } = require('../utils/jwt');
const { successResponse, errorResponse } = require('../utils/response');
const logger = require('../utils/logger');
const config = require('../config');

/**
 * 用户登录
 * @route POST /api/auth/login
 * @param {string} req.body.username - 用户名
 * @param {string} req.body.password - 密码
 */
const login = async (req, res) => {
  try {
    const { username, password } = req.body;

    logger.info('用户登录请求', { username });

    // 验证用户凭据
    const user = await User.verifyPassword(username, password);

    if (!user) {
      logger.warn('登录失败：用户名或密码错误', { username });
      return errorResponse(
        res,
        '用户名或密码错误',
        401,
        'INVALID_CREDENTIALS'
      );
    }

    // 生成JWT Token
    const tokenPayload = {
      userId: user.id,
      username: user.username,
      role: user.role,
    };

    const token = generateToken(tokenPayload);

    logger.info('用户登录成功', { userId: user.id, username: user.username });

    // 返回用户信息和Token
    return successResponse(
      res,
      {
        user: {
          id: user.id,
          username: user.username,
          email: user.email,
          name: user.name,
          role: user.role,
        },
        token,
        expiresIn: config.jwt.expiresIn,
      },
      '登录成功'
    );
  } catch (error) {
    logger.error('登录过程发生错误', { error: error.message, stack: error.stack });
    return errorResponse(
      res,
      '登录过程发生错误，请稍后重试',
      500,
      'LOGIN_ERROR'
    );
  }
};

/**
 * 获取当前用户信息
 * @route GET /api/auth/me
 */
const getCurrentUser = async (req, res) => {
  try {
    const userId = req.user.userId;

    const user = await User.findById(userId);

    if (!user) {
      logger.warn('用户不存在', { userId });
      return errorResponse(res, '用户不存在', 404, 'USER_NOT_FOUND');
    }

    logger.debug('获取当前用户信息成功', { userId });

    return successResponse(res, { user }, '获取用户信息成功');
  } catch (error) {
    logger.error('获取用户信息失败', { error: error.message });
    return errorResponse(
      res,
      '获取用户信息失败',
      500,
      'GET_USER_ERROR'
    );
  }
};

/**
 * 用户登出（客户端清除Token即可）
 * @route POST /api/auth/logout
 */
const logout = async (req, res) => {
  try {
    logger.info('用户登出', { userId: req.user?.userId });
    return successResponse(res, null, '登出成功');
  } catch (error) {
    logger.error('登出过程发生错误', { error: error.message });
    return errorResponse(res, '登出失败', 500, 'LOGOUT_ERROR');
  }
};

/**
 * 验证Token有效性
 * @route GET /api/auth/verify
 */
const verifyTokenEndpoint = async (req, res) => {
  try {
    return successResponse(
      res,
      {
        valid: true,
        user: req.user,
      },
      'Token有效'
    );
  } catch (error) {
    logger.error('Token验证失败', { error: error.message });
    return errorResponse(res, 'Token验证失败', 500, 'VERIFY_ERROR');
  }
};

module.exports = {
  login,
  getCurrentUser,
  logout,
  verifyTokenEndpoint,
};