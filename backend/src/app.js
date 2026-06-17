/**
 * 电商购物平台后端服务
 * 主应用入口
 */
const express = require('express');
const cors = require('cors');
const morgan = require('morgan');
const config = require('./config');
const logger = require('./utils/logger');
const { authRoutes, orderRoutes } = require('./routes');
const { errorHandler, notFoundHandler } = require('./middleware/validate');
const { initTestUsers } = require('./models/User');
const { initTestOrders } = require('./models/Order');

// 创建Express应用
const app = express();

// 中间件配置
// CORS配置
app.use(
  cors({
    origin: ['http://localhost:3000', 'http://localhost:5173', 'http://127.0.0.1:5173'],
    credentials: true,
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization'],
  })
);

// 请求日志
if (config.server.env !== 'test') {
  app.use(
    morgan('combined', {
      stream: {
        write: (message) => logger.info(message.trim()),
      },
    })
  );
}

// JSON解析
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));

// API路由
app.use('/api/auth', authRoutes);
app.use('/api/orders', orderRoutes);

// 健康检查端点
app.get('/health', (req, res) => {
  res.json({
    status: 'ok',
    timestamp: new Date().toISOString(),
    env: config.server.env,
  });
});

// API信息端点
app.get('/api', (req, res) => {
  res.json({
    name: '电商购物平台API',
    version: '1.0.0',
    endpoints: {
      auth: {
        login: 'POST /api/auth/login',
        logout: 'POST /api/auth/logout',
        me: 'GET /api/auth/me',
        verify: 'GET /api/auth/verify',
      },
      orders: {
        list: 'GET /api/orders',
        detail: 'GET /api/orders/:id',
        statuses: 'GET /api/orders/statuses',
        create: 'POST /api/orders',
        updateStatus: 'PUT /api/orders/:id/status',
      },
    },
  });
});

// 404处理
app.use(notFoundHandler);

// 错误处理
app.use(errorHandler);

// 初始化数据并启动服务器
const startServer = async () => {
  try {
    // 初始化测试数据
    await initTestUsers();
    initTestOrders();

    // 启动服务器
    const server = app.listen(config.server.port, () => {
      logger.info(`服务器启动成功`, {
        port: config.server.port,
        env: config.server.env,
      });
      logger.info(`API文档: http://localhost:${config.server.port}/api`);
      logger.info(`健康检查: http://localhost:${config.server.port}/health`);
    });

    return server;
  } catch (error) {
    logger.error('服务器启动失败', { error: error.message, stack: error.stack });
    process.exit(1);
  }
};

// 处理未捕获的异常
process.on('uncaughtException', (error) => {
  logger.error('未捕获的异常', { error: error.message, stack: error.stack });
  process.exit(1);
});

// 处理未处理的Promise拒绝
process.on('unhandledRejection', (reason, promise) => {
  logger.error('未处理的Promise拒绝', { reason, promise });
});

// 导出应用和启动函数
module.exports = { app, startServer };

// 如果直接运行此文件，则启动服务器
if (require.main === module) {
  startServer();
}