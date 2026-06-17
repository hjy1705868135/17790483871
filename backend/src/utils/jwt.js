/**
 * JWT工具模块
 * 提供Token生成、验证、解析功能
 */
const jwt = require('jsonwebtoken');
const config = require('../config');
const logger = require('./logger');

/**
 * 生成JWT Token
 * @param {Object} payload - Token载荷数据
 * @returns {string} JWT Token
 */
const generateToken = (payload) => {
  try {
    const token = jwt.sign(payload, config.jwt.secret, {
      expiresIn: config.jwt.expiresIn,
    });
    logger.debug('Token生成成功', { userId: payload.userId });
    return token;
  } catch (error) {
    logger.error('Token生成失败', { error: error.message });
    throw new Error('Token生成失败');
  }
};

/**
 * 验证JWT Token
 * @param {string} token - JWT Token
 * @returns {Object|null} 解析后的载荷数据，验证失败返回null
 */
const verifyToken = (token) => {
  try {
    const decoded = jwt.verify(token, config.jwt.secret);
    logger.debug('Token验证成功', { userId: decoded.userId });
    return decoded;
  } catch (error) {
    if (error.name === 'TokenExpiredError') {
      logger.warn('Token已过期', { expiredAt: error.expiredAt });
    } else if (error.name === 'JsonWebTokenError') {
      logger.warn('Token无效', { message: error.message });
    } else {
      logger.error('Token验证失败', { error: error.message });
    }
    return null;
  }
};

/**
 * 解析JWT Token（不验证签名）
 * @param {string} token - JWT Token
 * @returns {Object|null} 解析后的载荷数据
 */
const decodeToken = (token) => {
  try {
    return jwt.decode(token);
  } catch (error) {
    logger.error('Token解析失败', { error: error.message });
    return null;
  }
};

/**
 * 获取Token过期时间
 * @param {string} token - JWT Token
 * @returns {Date|null} 过期时间
 */
const getTokenExpiration = (token) => {
  const decoded = decodeToken(token);
  if (decoded && decoded.exp) {
    return new Date(decoded.exp * 1000);
  }
  return null;
};

module.exports = {
  generateToken,
  verifyToken,
  decodeToken,
  getTokenExpiration,
};