/**
 * 应用配置模块
 * 集中管理所有配置项，支持环境变量覆盖
 */
require('dotenv').config();

const config = {
  // 服务器配置
  server: {
    port: parseInt(process.env.PORT, 10) || 3001,
    env: process.env.NODE_ENV || 'development',
  },

  // JWT配置
  jwt: {
    secret: process.env.JWT_SECRET || 'default-secret-key-change-in-production',
    expiresIn: process.env.JWT_EXPIRES_IN || '24h',
  },

  // 日志配置
  log: {
    level: process.env.LOG_LEVEL || 'info',
  },

  // 分页配置
  pagination: {
    defaultPage: 1,
    defaultPageSize: 10,
    maxPageSize: 100,
  },
};

module.exports = config;