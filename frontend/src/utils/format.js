/**
 * 格式化工具模块
 */

/**
 * 格式化日期
 * @param {string|Date} date - 日期
 * @param {string} format - 格式
 * @returns {string} 格式化后的日期字符串
 */
const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return '';
  
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';

  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
};

/**
 * 格式化金额
 * @param {number} amount - 金额
 * @returns {string} 格式化后的金额字符串
 */
const formatAmount = (amount) => {
  if (amount === null || amount === undefined) return '¥0.00';
  return `¥${parseFloat(amount).toFixed(2)}`;
};

/**
 * 格式化订单状态
 * @param {string} status - 状态码
 * @returns {string} 状态文本
 */
const formatOrderStatus = (status) => {
  const statusMap = {
    pending: '待支付',
    paid: '已支付',
    shipped: '已发货',
    delivered: '已送达',
    cancelled: '已取消',
  };
  return statusMap[status] || status;
};

/**
 * 获取状态样式类名
 * @param {string} status - 状态码
 * @returns {string} 样式类名
 */
const getStatusClassName = (status) => {
  const classMap = {
    pending: 'status-pending',
    paid: 'status-paid',
    shipped: 'status-shipped',
    delivered: 'status-delivered',
    cancelled: 'status-cancelled',
  };
  return classMap[status] || '';
};

export {
  formatDate,
  formatAmount,
  formatOrderStatus,
  getStatusClassName,
};