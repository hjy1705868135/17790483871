/**
 * 登录页面组件
 */
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import { saveToken, saveUser } from '../utils/auth';

const LoginPage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // 处理输入变化
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // 清除错误提示
    if (error) setError('');
  };

  // 处理表单提交
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // 表单验证
    if (!formData.username.trim()) {
      setError('请输入用户名');
      return;
    }
    if (!formData.password) {
      setError('请输入密码');
      return;
    }
    if (formData.password.length < 6) {
      setError('密码长度至少6位');
      return;
    }

    setLoading(true);

    try {
      // 调用登录API
      const response = await authAPI.login(formData.username, formData.password);

      if (response.success) {
        // 保存Token和用户信息
        saveToken(response.data.token);
        saveUser(response.data.user);

        // 跳转到订单列表页
        navigate('/orders');
      } else {
        setError(response.message || '登录失败，请重试');
      }
    } catch (err) {
      setError(err.message || '登录失败，请检查网络连接');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1 className="login-title">电商购物平台</h1>
        
        {error && <div className="message message-error">{error}</div>}
        
        <form className="login-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="username">
              用户名
            </label>
            <input
              type="text"
              id="username"
              name="username"
              className="form-input"
              placeholder="请输入用户名"
              value={formData.username}
              onChange={handleChange}
              disabled={loading}
              autoComplete="username"
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">
              密码
            </label>
            <input
              type="password"
              id="password"
              name="password"
              className="form-input"
              placeholder="请输入密码"
              value={formData.password}
              onChange={handleChange}
              disabled={loading}
              autoComplete="current-password"
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '16px' }}
            disabled={loading}
          >
            {loading ? '登录中...' : '登录'}
          </button>
        </form>

        <div style={{ marginTop: '24px', textAlign: 'center', color: '#999', fontSize: '12px' }}>
          <p>测试账号：</p>
          <p>管理员：admin / admin123</p>
          <p>用户：user1 / user123</p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;