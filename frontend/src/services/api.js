/**
 * API服务模块
 * 封装所有HTTP请求
 */
import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器 - 添加Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    // 处理401错误 - Token过期或无效
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // 如果不在登录页面，则跳转到登录页
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error.response?.data || error);
  }
);

// 认证API
const authAPI = {
  /**
   * 用户登录
   * @param {string} username - 用户名
   * @param {string} password - 密码
   * @returns {Promise} 登录结果
   */
  login: (username, password) => api.post('/auth/login', { username, password }),

  /**
   * 用户登出
   * @returns {Promise} 登出结果
   */
  logout: () => api.post('/auth/logout'),

  /**
   * 获取当前用户信息
   * @returns {Promise} 用户信息
   */
  getCurrentUser: () => api.get('/auth/me'),

  /**
   * 验证Token有效性
   * @returns {Promise} 验证结果
   */
  verifyToken: () => api.get('/auth/verify'),
};

// 订单API
const orderAPI = {
  /**
   * 获取订单列表
   * @param {Object} params - 查询参数
   * @returns {Promise} 订单列表
   */
  getOrders: (params = {}) => api.get('/orders', { params }),

  /**
   * 获取订单详情
   * @param {string} id - 订单ID
   * @returns {Promise} 订单详情
   */
  getOrderById: (id) => api.get(`/orders/${id}`),

  /**
   * 获取订单状态列表
   * @returns {Promise} 状态列表
   */
  getStatuses: () => api.get('/orders/statuses'),

  /**
   * 创建订单
   * @param {Object} orderData - 订单数据
   * @returns {Promise} 创建结果
   */
  createOrder: (orderData) => api.post('/orders', orderData),

  /**
   * 更新订单状态
   * @param {string} id - 订单ID
   * @param {string} status - 新状态
   * @returns {Promise} 更新结果
   */
  updateStatus: (id, status) => api.put(`/orders/${id}/status`, { status }),
};

export { api, authAPI, orderAPI };