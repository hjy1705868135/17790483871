/**
 * 订单列表页面组件
 * 支持分页、筛选、排序功能
 */
import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderAPI } from '../services/api';
import { formatDate, formatAmount, formatOrderStatus, getStatusClassName } from '../utils/format';

const OrderListPage = () => {
  const navigate = useNavigate();
  
  // 订单数据状态
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // 分页状态
  const [pagination, setPagination] = useState({
    page: 1,
    pageSize: 10,
    total: 0,
    totalPages: 0,
  });
  
  // 筛选状态
  const [filters, setFilters] = useState({
    status: '',
    orderNo: '',
    userName: '',
    startDate: '',
    endDate: '',
    minAmount: '',
    maxAmount: '',
  });
  
  // 排序状态
  const [sortConfig, setSortConfig] = useState({
    sortBy: 'createdAt',
    sortOrder: 'desc',
  });

  // 订单状态选项
  const [statusOptions, setStatusOptions] = useState([]);

  // 获取订单状态列表
  useEffect(() => {
    const fetchStatuses = async () => {
      try {
        const response = await orderAPI.getStatuses();
        if (response.success) {
          setStatusOptions(response.data.statuses);
        }
      } catch (err) {
        console.error('获取状态列表失败:', err);
      }
    };
    fetchStatuses();
  }, []);

  // 获取订单列表
  const fetchOrders = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      // 构建查询参数
      const params = {
        page: pagination.page,
        pageSize: pagination.pageSize,
        sortBy: sortConfig.sortBy,
        sortOrder: sortConfig.sortOrder,
      };

      // 添加筛选条件
      if (filters.status) params.status = filters.status;
      if (filters.orderNo) params.orderNo = filters.orderNo;
      if (filters.userName) params.userName = filters.userName;
      if (filters.startDate) params.startDate = filters.startDate;
      if (filters.endDate) params.endDate = filters.endDate;
      if (filters.minAmount) params.minAmount = filters.minAmount;
      if (filters.maxAmount) params.maxAmount = filters.maxAmount;

      const response = await orderAPI.getOrders(params);

      if (response.success) {
        setOrders(response.data.items);
        setPagination((prev) => ({
          ...prev,
          total: response.data.pagination.total,
          totalPages: response.data.pagination.totalPages,
        }));
      } else {
        setError(response.message || '获取订单列表失败');
      }
    } catch (err) {
      setError(err.message || '获取订单列表失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  }, [pagination.page, pagination.pageSize, sortConfig, filters]);

  // 监听分页和排序变化，重新获取数据
  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  // 处理筛选条件变化
  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // 应用筛选
  const applyFilters = () => {
    setPagination((prev) => ({ ...prev, page: 1 }));
    fetchOrders();
  };

  // 重置筛选
  const resetFilters = () => {
    setFilters({
      status: '',
      orderNo: '',
      userName: '',
      startDate: '',
      endDate: '',
      minAmount: '',
      maxAmount: '',
    });
    setPagination((prev) => ({ ...prev, page: 1 }));
  };

  // 处理排序
  const handleSort = (field) => {
    setSortConfig((prev) => ({
      sortBy: field,
      sortOrder: prev.sortBy === field && prev.sortOrder === 'desc' ? 'asc' : 'desc',
    }));
    setPagination((prev) => ({ ...prev, page: 1 }));
  };

  // 处理分页
  const handlePageChange = (newPage) => {
    setPagination((prev) => ({ ...prev, page: newPage }));
  };

  // 渲染排序图标
  const renderSortIcon = (field) => {
    if (sortConfig.sortBy !== field) return '↕';
    return sortConfig.sortOrder === 'asc' ? '↑' : '↓';
  };

  // 渲染分页组件
  const renderPagination = () => {
    const pages = [];
    const maxVisiblePages = 5;
    let startPage = Math.max(1, pagination.page - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(pagination.totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <button
          key={i}
          className={`pagination-btn ${pagination.page === i ? 'active' : ''}`}
          onClick={() => handlePageChange(i)}
        >
          {i}
        </button>
      );
    }

    return (
      <div className="pagination">
        <button
          className="pagination-btn"
          onClick={() => handlePageChange(1)}
          disabled={pagination.page === 1}
        >
          首页
        </button>
        <button
          className="pagination-btn"
          onClick={() => handlePageChange(pagination.page - 1)}
          disabled={pagination.page === 1}
        >
          上一页
        </button>
        {pages}
        <button
          className="pagination-btn"
          onClick={() => handlePageChange(pagination.page + 1)}
          disabled={pagination.page === pagination.totalPages}
        >
          下一页
        </button>
        <button
          className="pagination-btn"
          onClick={() => handlePageChange(pagination.totalPages)}
          disabled={pagination.page === pagination.totalPages}
        >
          末页
        </button>
        <span style={{ marginLeft: '16px', color: '#666' }}>
          共 {pagination.total} 条记录
        </span>
      </div>
    );
  };

  return (
    <div className="container">
      <h2 style={{ marginBottom: '20px' }}>订单列表</h2>

      {/* 筛选器 */}
      <div className="filters">
        <div className="filter-group">
          <label className="filter-label">订单状态</label>
          <select
            name="status"
            className="filter-select"
            value={filters.status}
            onChange={handleFilterChange}
          >
            <option value="">全部状态</option>
            {statusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label className="filter-label">订单号</label>
          <input
            type="text"
            name="orderNo"
            className="filter-input"
            placeholder="输入订单号"
            value={filters.orderNo}
            onChange={handleFilterChange}
          />
        </div>

        <div className="filter-group">
          <label className="filter-label">用户名</label>
          <input
            type="text"
            name="userName"
            className="filter-input"
            placeholder="输入用户名"
            value={filters.userName}
            onChange={handleFilterChange}
          />
        </div>

        <div className="filter-group">
          <label className="filter-label">开始日期</label>
          <input
            type="date"
            name="startDate"
            className="filter-input"
            value={filters.startDate}
            onChange={handleFilterChange}
          />
        </div>

        <div className="filter-group">
          <label className="filter-label">结束日期</label>
          <input
            type="date"
            name="endDate"
            className="filter-input"
            value={filters.endDate}
            onChange={handleFilterChange}
          />
        </div>

        <div className="filter-group">
          <label className="filter-label">最小金额</label>
          <input
            type="number"
            name="minAmount"
            className="filter-input"
            placeholder="最小金额"
            value={filters.minAmount}
            onChange={handleFilterChange}
            min="0"
            step="0.01"
          />
        </div>

        <div className="filter-group">
          <label className="filter-label">最大金额</label>
          <input
            type="number"
            name="maxAmount"
            className="filter-input"
            placeholder="最大金额"
            value={filters.maxAmount}
            onChange={handleFilterChange}
            min="0"
            step="0.01"
          />
        </div>

        <div className="filter-group" style={{ alignSelf: 'flex-end' }}>
          <button className="btn btn-primary" onClick={applyFilters}>
            查询
          </button>
          <button
            className="btn btn-secondary"
            onClick={resetFilters}
            style={{ marginLeft: '8px' }}
          >
            重置
          </button>
        </div>
      </div>

      {/* 错误提示 */}
      {error && <div className="message message-error">{error}</div>}

      {/* 加载状态 */}
      {loading ? (
        <div className="loading">
          <div className="spinner"></div>
        </div>
      ) : orders.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📦</div>
          <p>暂无订单数据</p>
        </div>
      ) : (
        <>
          {/* 订单表格 */}
          <div className="card">
            <table className="table">
              <thead>
                <tr>
                  <th
                    style={{ cursor: 'pointer' }}
                    onClick={() => handleSort('orderNo')}
                  >
                    订单号 {renderSortIcon('orderNo')}
                  </th>
                  <th>用户名</th>
                  <th>商品数量</th>
                  <th
                    style={{ cursor: 'pointer' }}
                    onClick={() => handleSort('totalAmount')}
                  >
                    总金额 {renderSortIcon('totalAmount')}
                  </th>
                  <th
                    style={{ cursor: 'pointer' }}
                    onClick={() => handleSort('status')}
                  >
                    状态 {renderSortIcon('status')}
                  </th>
                  <th
                    style={{ cursor: 'pointer' }}
                    onClick={() => handleSort('createdAt')}
                  >
                    创建时间 {renderSortIcon('createdAt')}
                  </th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.id}>
                    <td>{order.orderNo}</td>
                    <td>{order.userName}</td>
                    <td>{order.items?.length || 0}</td>
                    <td>{formatAmount(order.totalAmount)}</td>
                    <td>
                      <span className={`status-tag ${getStatusClassName(order.status)}`}>
                        {formatOrderStatus(order.status)}
                      </span>
                    </td>
                    <td>{formatDate(order.createdAt)}</td>
                    <td>
                      <button
                        className="btn btn-secondary"
                        style={{ padding: '4px 12px', fontSize: '12px' }}
                        onClick={() => navigate(`/orders/${order.id}`)}
                      >
                        查看详情
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* 分页 */}
          {pagination.totalPages > 1 && renderPagination()}
        </>
      )}
    </div>
  );
};

export default OrderListPage;