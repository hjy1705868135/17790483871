/**
 * 主应用组件
 */
import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { authAPI } from './services/api';
import { isAuthenticated, clearAuth } from './utils/auth';
import Header from './components/Header';
import LoginPage from './pages/LoginPage';
import OrderListPage from './pages/OrderListPage';
import OrderDetailPage from './pages/OrderDetailPage';
import ProtectedRoute from './components/ProtectedRoute';

const App = () => {
  const [initialized, setInitialized] = useState(false);

  // 应用初始化时验证Token
  useEffect(() => {
    const initAuth = async () => {
      if (isAuthenticated()) {
        try {
          // 验证Token是否有效
          await authAPI.verifyToken();
        } catch (err) {
          // Token无效，清除认证信息
          console.error('Token验证失败:', err);
          clearAuth();
        }
      }
      setInitialized(true);
    };

    initAuth();
  }, []);

  if (!initialized) {
    return (
      <div className="loading" style={{ height: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <BrowserRouter>
      <Routes>
        {/* 登录页 */}
        <Route path="/login" element={<LoginPage />} />

        {/* 受保护的路由 */}
        <Route
          path="/*"
          element={
            <ProtectedRoute>
              <>
                <Header />
                <Routes>
                  <Route path="/" element={<Navigate to="/orders" replace />} />
                  <Route path="/orders" element={<OrderListPage />} />
                  <Route path="/orders/:id" element={<OrderDetailPage />} />
                </Routes>
              </>
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
};

export default App;