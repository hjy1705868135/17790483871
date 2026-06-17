/**
 * 认证工具模块
 * 管理用户认证状态和Token
 */

const TOKEN_KEY = 'token';
const USER_KEY = 'user';

/**
 * 保存Token到本地存储
 * @param {string} token - JWT Token
 */
const saveToken = (token) => {
  localStorage.setItem(TOKEN_KEY, token);
};

/**
 * 获取Token
 * @returns {string|null} JWT Token
 */
const getToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

/**
 * 移除Token
 */
const removeToken = () => {
  localStorage.removeItem(TOKEN_KEY);
};

/**
 * 保存用户信息到本地存储
 * @param {Object} user - 用户信息
 */
const saveUser = (user) => {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
};

/**
 * 获取用户信息
 * @returns {Object|null} 用户信息
 */
const getUser = () => {
  const userStr = localStorage.getItem(USER_KEY);
  return userStr ? JSON.parse(userStr) : null;
};

/**
 * 移除用户信息
 */
const removeUser = () => {
  localStorage.removeItem(USER_KEY);
};

/**
 * 检查是否已登录
 * @returns {boolean} 是否已登录
 */
const isAuthenticated = () => {
  return !!getToken();
};

/**
 * 清除所有认证信息
 */
const clearAuth = () => {
  removeToken();
  removeUser();
};

/**
 * 解析JWT Token（不验证签名）
 * @param {string} token - JWT Token
 * @returns {Object|null} 解析后的载荷
 */
const decodeToken = (token) => {
  try {
    if (!token) return null;
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    const payload = parts[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded);
  } catch (error) {
    console.error('Token解析失败:', error);
    return null;
  }
};

/**
 * 检查Token是否过期
 * @param {string} token - JWT Token
 * @returns {boolean} 是否过期
 */
const isTokenExpired = (token) => {
  const decoded = decodeToken(token);
  if (!decoded || !decoded.exp) return true;
  return decoded.exp * 1000 < Date.now();
};

/**
 * 获取Token剩余有效时间（秒）
 * @param {string} token - JWT Token
 * @returns {number} 剩余有效时间（秒），如果过期返回0
 */
const getTokenRemainingTime = (token) => {
  const decoded = decodeToken(token);
  if (!decoded || !decoded.exp) return 0;
  const remaining = decoded.exp * 1000 - Date.now();
  return remaining > 0 ? Math.floor(remaining / 1000) : 0;
};

export {
  saveToken,
  getToken,
  removeToken,
  saveUser,
  getUser,
  removeUser,
  isAuthenticated,
  clearAuth,
  decodeToken,
  isTokenExpired,
  getTokenRemainingTime,
};