/**
 * 头部导航组件
 */
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import { getUser, clearAuth } from '../utils/auth';

const Header = () => {
  const navigate = useNavigate();
  const user = getUser();

  const handleLogout = async () => {
    try {
      await authAPI.logout();
    } catch (err) {
      console.error('登出失败:', err);
    } finally {
      clearAuth();
      navigate('/login');
    }
  };

  return (
    <header className="header">
      <div className="header-content">
        <h1 className="header-title" onClick={() => navigate('/orders')} style={{ cursor: 'pointer' }}>
          电商购物平台
        </h1>
        <div className="header-user">
          {user && (
            <>
              <span className="user-info">
                欢迎，{user.name || user.username}
                {user.role === 'admin' && ' (管理员)'}
              </span>
              <button
                className="btn btn-secondary"
                onClick={handleLogout}
                style={{ padding: '6px 16px', fontSize: '12px' }}
              >
                退出登录
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;