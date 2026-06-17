/**
 * 认证路由
 */
const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { authMiddleware } = require('../middleware/auth');
const { loginValidationRules } = require('../middleware/validate');

/**
 * @route POST /api/auth/login
 * @desc 用户登录
 * @access Public
 */
router.post('/login', loginValidationRules, authController.login);

/**
 * @route POST /api/auth/logout
 * @desc 用户登出
 * @access Private
 */
router.post('/logout', authMiddleware, authController.logout);

/**
 * @route GET /api/auth/me
 * @desc 获取当前用户信息
 * @access Private
 */
router.get('/me', authMiddleware, authController.getCurrentUser);

/**
 * @route GET /api/auth/verify
 * @desc 验证Token有效性
 * @access Private
 */
router.get('/verify', authMiddleware, authController.verifyTokenEndpoint);

module.exports = router;