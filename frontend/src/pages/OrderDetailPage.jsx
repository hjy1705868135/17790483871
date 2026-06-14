/**
 * 订单详情页面组件
 */
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { orderAPI } from '../services/api';
import { formatDate, formatAmount, formatOrderStatus, getStatusClassName } from '../utils/format';

const OrderDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchOrder = async () => {
      setLoading(true);
      setError('');

      try {
        const response = await orderAPI.getOrderById(id);

        if (response.success) {
          setOrder(response.data.order);
        } else {
          setError(response.message || '获取订单详情失败');
        }
      } catch (err) {
        setError(err.message || '获取订单详情失败，请稍后重试');
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();
  }, [id]);

  if (loading) {
    return (
      <div className="container">
        <div className="loading">
          <div className="spinner"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container">
        <div className="message message-error">{error}</div>
        <button className="btn btn-secondary" onClick={() => navigate('/orders')}>
          返回列表
        </button>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="container">
        <div className="empty-state">
          <div className="empty-state-icon">📦</div>
          <p>订单不存在</p>
          <button className="btn btn-primary" onClick={() => navigate('/orders')}>
            返回列表
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2>订单详情</h2>
        <button className="btn btn-secondary" onClick={() => navigate('/orders')}>
          返回列表
        </button>
      </div>

      <div className="card">
        {/* 基本信息 */}
        <div className="order-detail-section">
          <h3 className="order-detail-title">基本信息</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '16px' }}>
            <div>
              <strong>订单号：</strong>
              {order.orderNo}
            </div>
            <div>
              <strong>订单状态：</strong>
              <span className={`status-tag ${getStatusClassName(order.status)}`} style={{ marginLeft: '8px' }}>
                {formatOrderStatus(order.status)}
              </span>
            </div>
            <div>
              <strong>用户名：</strong>
              {order.userName}
            </div>
            <div>
              <strong>总金额：</strong>
              <span style={{ color: '#ff4d4f', fontWeight: 'bold' }}>
                {formatAmount(order.totalAmount)}
              </span>
            </div>
            <div>
              <strong>创建时间：</strong>
              {formatDate(order.createdAt)}
            </div>
            <div>
              <strong>更新时间：</strong>
              {formatDate(order.updatedAt)}
            </div>
          </div>
        </div>

        {/* 收货地址 */}
        <div className="order-detail-section">
          <h3 className="order-detail-title">收货地址</h3>
          <p>{order.shippingAddress}</p>
        </div>

        {/* 商品列表 */}
        <div className="order-detail-section">
          <h3 className="order-detail-title">商品列表</h3>
          <table className="table">
            <thead>
              <tr>
                <th>商品名称</th>
                <th>单价</th>
                <th>数量</th>
                <th>小计</th>
              </tr>
            </thead>
            <tbody>
              {order.items && order.items.map((item, index) => (
                <tr key={index}>
                  <td>{item.productName}</td>
                  <td>{formatAmount(item.price)}</td>
                  <td>{item.quantity}</td>
                  <td>{formatAmount(item.price * item.quantity)}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td colSpan="3" style={{ textAlign: 'right', fontWeight: 'bold' }}>
                  合计：
                </td>
                <td style={{ color: '#ff4d4f', fontWeight: 'bold' }}>
                  {formatAmount(order.totalAmount)}
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  );
};

export default OrderDetailPage;