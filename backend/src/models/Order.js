/**
 * 订单模型
 * 模拟数据库订单表
 */
const { v4: uuidv4 } = require('uuid');
const logger = require('../utils/logger');

// 模拟订单数据库
const orders = new Map();

// 订单状态枚举
const OrderStatus = {
  PENDING: 'pending',      // 待支付
  PAID: 'paid',           // 已支付
  SHIPPED: 'shipped',     // 已发货
  DELIVERED: 'delivered', // 已送达
  CANCELLED: 'cancelled', // 已取消
};

// 订单状态中文映射
const OrderStatusText = {
  pending: '待支付',
  paid: '已支付',
  shipped: '已发货',
  delivered: '已送达',
  cancelled: '已取消',
};

// 初始化测试订单数据
const initTestOrders = () => {
  const testOrders = [
    {
      id: uuidv4(),
      orderNo: 'ORD20240101001',
      userId: 'user-1',
      userName: '张三',
      status: OrderStatus.DELIVERED,
      totalAmount: 299.00,
      items: [
        { productId: 'p1', productName: '商品A', quantity: 2, price: 99.50 },
        { productId: 'p2', productName: '商品B', quantity: 1, price: 100.00 },
      ],
      shippingAddress: '北京市朝阳区xxx街道xxx号',
      createdAt: new Date('2024-01-01T10:00:00Z'),
      updatedAt: new Date('2024-01-05T15:30:00Z'),
    },
    {
      id: uuidv4(),
      orderNo: 'ORD20240102001',
      userId: 'user-1',
      userName: '张三',
      status: OrderStatus.SHIPPED,
      totalAmount: 599.00,
      items: [
        { productId: 'p3', productName: '商品C', quantity: 1, price: 599.00 },
      ],
      shippingAddress: '北京市朝阳区xxx街道xxx号',
      createdAt: new Date('2024-01-02T14:30:00Z'),
      updatedAt: new Date('2024-01-03T09:00:00Z'),
    },
    {
      id: uuidv4(),
      orderNo: 'ORD20240103001',
      userId: 'user-2',
      userName: '李四',
      status: OrderStatus.PAID,
      totalAmount: 1299.00,
      items: [
        { productId: 'p4', productName: '商品D', quantity: 1, price: 1299.00 },
      ],
      shippingAddress: '上海市浦东新区xxx路xxx号',
      createdAt: new Date('2024-01-03T16:20:00Z'),
      updatedAt: new Date('2024-01-03T16:20:00Z'),
    },
    {
      id: uuidv4(),
      orderNo: 'ORD20240104001',
      userId: 'user-2',
      userName: '李四',
      status: OrderStatus.PENDING,
      totalAmount: 89.00,
      items: [
        { productId: 'p5', productName: '商品E', quantity: 3, price: 29.67 },
      ],
      shippingAddress: '上海市浦东新区xxx路xxx号',
      createdAt: new Date('2024-01-04T09:15:00Z'),
      updatedAt: new Date('2024-01-04T09:15:00Z'),
    },
    {
      id: uuidv4(),
      orderNo: 'ORD20240105001',
      userId: 'user-3',
      userName: '王五',
      status: OrderStatus.CANCELLED,
      totalAmount: 459.00,
      items: [
        { productId: 'p6', productName: '商品F', quantity: 1, price: 459.00 },
      ],
      shippingAddress: '广州市天河区xxx路xxx号',
      createdAt: new Date('2024-01-05T11:45:00Z'),
      updatedAt: new Date('2024-01-05T14:00:00Z'),
    },
    {
      id: uuidv4(),
      orderNo: 'ORD20240106001',
      userId: 'user-1',
      userName: '张三',
      status: OrderStatus.PENDING,
      totalAmount: 199.00,
      items: [
        { productId: 'p7', productName: '商品G', quantity: 2, price: 99.50 },
      ],
      shippingAddress: '北京市朝阳区xxx街道xxx号',
      createdAt: new Date('2024-01-06T08:30:00Z'),
      updatedAt: new Date('2024-01-06T08:30:00Z'),
    },
    {
      id: uuidv4(),
      orderNo: 'ORD20240107001',
      userId: 'user-3',
      userName: '王五',
      status: OrderStatus.DELIVERED,
      totalAmount: 799.00,
      items: [
        { productId: 'p8', productName: '商品H', quantity: 1, price: 799.00 },
      ],
      shippingAddress: '广州市天河区xxx路xxx号',
      createdAt: new Date('2024-01-07T13:00:00Z'),
      updatedAt: new Date('2024-01-10T16:00:00Z'),
    },
    {
      id: uuidv4(),
      orderNo: 'ORD20240108001',
      userId: 'user-2',
      userName: '李四',
      status: OrderStatus.SHIPPED,
      totalAmount: 399.00,
      items: [
        { productId: 'p9', productName: '商品I', quantity: 1, price: 399.00 },
      ],
      shippingAddress: '上海市浦东新区xxx路xxx号',
      createdAt: new Date('2024-01-08T10:20:00Z'),
      updatedAt: new Date('2024-01-09T08:00:00Z'),
    },
    {
      id: uuidv4(),
      orderNo: 'ORD20240109001',
      userId: 'user-1',
      userName: '张三',
      status: OrderStatus.PAID,
      totalAmount: 1599.00,
      items: [
        { productId: 'p10', productName: '商品J', quantity: 1, price: 1599.00 },
      ],
      shippingAddress: '北京市朝阳区xxx街道xxx号',
      createdAt: new Date('2024-01-09T15:45:00Z'),
      updatedAt: new Date('2024-01-09T15:45:00Z'),
    },
    {
      id: uuidv4(),
      orderNo: 'ORD20240110001',
      userId: 'user-3',
      userName: '王五',
      status: OrderStatus.PENDING,
      totalAmount: 259.00,
      items: [
        { productId: 'p11', productName: '商品K', quantity: 2, price: 129.50 },
      ],
      shippingAddress: '广州市天河区xxx路xxx号',
      createdAt: new Date('2024-01-10T09:00:00Z'),
      updatedAt: new Date('2024-01-10T09:00:00Z'),
    },
    // 添加更多测试数据用于分页测试
    ...generateMoreOrders(15),
  ];

  testOrders.forEach((order) => {
    orders.set(order.id, order);
  });

  logger.info('测试订单初始化完成', { count: testOrders.length });
};

// 生成更多测试订单
function generateMoreOrders(count) {
  const statuses = Object.values(OrderStatus);
  const userIds = ['user-1', 'user-2', 'user-3'];
  const userNames = ['张三', '李四', '王五'];
  const result = [];

  for (let i = 0; i < count; i++) {
    const userIndex = Math.floor(Math.random() * 3);
    const orderDate = new Date(2024, 0, 11 + i);
    result.push({
      id: uuidv4(),
      orderNo: `ORD202401${String(11 + i).padStart(2, '0')}01`,
      userId: userIds[userIndex],
      userName: userNames[userIndex],
      status: statuses[Math.floor(Math.random() * statuses.length)],
      totalAmount: Math.floor(Math.random() * 2000) + 100,
      items: [
        {
          productId: `p${20 + i}`,
          productName: `测试商品${20 + i}`,
          quantity: Math.floor(Math.random() * 5) + 1,
          price: Math.floor(Math.random() * 500) + 50,
        },
      ],
      shippingAddress: '测试地址',
      createdAt: orderDate,
      updatedAt: orderDate,
    });
  }

  return result;
}

/**
 * 订单类
 */
class Order {
  /**
   * 查询订单列表（支持分页、筛选、排序）
   * @param {Object} options - 查询选项
   * @returns {Object} 分页结果
   */
  static async findAll(options = {}) {
    const {
      page = 1,
      pageSize = 10,
      status,
      userId,
      userName,
      orderNo,
      startDate,
      endDate,
      minAmount,
      maxAmount,
      sortBy = 'createdAt',
      sortOrder = 'desc',
    } = options;

    let filteredOrders = Array.from(orders.values());

    // 筛选条件
    if (status) {
      filteredOrders = filteredOrders.filter((o) => o.status === status);
    }
    if (userId) {
      filteredOrders = filteredOrders.filter((o) => o.userId === userId);
    }
    if (userName) {
      filteredOrders = filteredOrders.filter((o) =>
        o.userName.includes(userName)
      );
    }
    if (orderNo) {
      filteredOrders = filteredOrders.filter((o) =>
        o.orderNo.includes(orderNo)
      );
    }
    if (startDate) {
      const start = new Date(startDate);
      filteredOrders = filteredOrders.filter((o) => new Date(o.createdAt) >= start);
    }
    if (endDate) {
      const end = new Date(endDate);
      end.setHours(23, 59, 59, 999);
      filteredOrders = filteredOrders.filter((o) => new Date(o.createdAt) <= end);
    }
    if (minAmount !== undefined) {
      filteredOrders = filteredOrders.filter((o) => o.totalAmount >= minAmount);
    }
    if (maxAmount !== undefined) {
      filteredOrders = filteredOrders.filter((o) => o.totalAmount <= maxAmount);
    }

    // 排序
    filteredOrders.sort((a, b) => {
      let comparison = 0;
      
      if (sortBy === 'createdAt' || sortBy === 'updatedAt') {
        comparison = new Date(a[sortBy]) - new Date(b[sortBy]);
      } else if (sortBy === 'totalAmount') {
        comparison = a.totalAmount - b.totalAmount;
      } else if (sortBy === 'orderNo') {
        comparison = a.orderNo.localeCompare(b.orderNo);
      } else {
        comparison = String(a[sortBy]).localeCompare(String(b[sortBy]));
      }

      return sortOrder === 'desc' ? -comparison : comparison;
    });

    // 分页
    const total = filteredOrders.length;
    const totalPages = Math.ceil(total / pageSize);
    const startIndex = (page - 1) * pageSize;
    const paginatedOrders = filteredOrders.slice(startIndex, startIndex + pageSize);

    logger.debug('订单查询完成', {
      total,
      page,
      pageSize,
      filters: { status, userId, userName, orderNo, startDate, endDate, minAmount, maxAmount },
    });

    return {
      items: paginatedOrders,
      total,
      page,
      pageSize,
      totalPages,
    };
  }

  /**
   * 根据ID查找订单
   * @param {string} id - 订单ID
   * @returns {Object|null} 订单对象
   */
  static async findById(id) {
    return orders.get(id) || null;
  }

  /**
   * 根据订单号查找订单
   * @param {string} orderNo - 订单号
   * @returns {Object|null} 订单对象
   */
  static async findByOrderNo(orderNo) {
    for (const order of orders.values()) {
      if (order.orderNo === orderNo) {
        return order;
      }
    }
    return null;
  }

  /**
   * 创建新订单
   * @param {Object} orderData - 订单数据
   * @returns {Object} 创建的订单对象
   */
  static async create(orderData) {
    const newOrder = {
      id: uuidv4(),
      orderNo: `ORD${Date.now()}`,
      ...orderData,
      status: orderData.status || OrderStatus.PENDING,
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    orders.set(newOrder.id, newOrder);
    logger.info('订单创建成功', { orderId: newOrder.id, orderNo: newOrder.orderNo });

    return newOrder;
  }

  /**
   * 更新订单状态
   * @param {string} id - 订单ID
   * @param {string} status - 新状态
   * @returns {Object|null} 更新后的订单对象
   */
  static async updateStatus(id, status) {
    const order = orders.get(id);
    if (!order) {
      return null;
    }

    order.status = status;
    order.updatedAt = new Date();
    orders.set(id, order);

    logger.info('订单状态更新成功', { orderId: id, newStatus: status });
    return order;
  }
}

module.exports = {
  Order,
  OrderStatus,
  OrderStatusText,
  initTestOrders,
};