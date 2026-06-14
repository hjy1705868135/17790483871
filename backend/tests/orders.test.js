/**
 * 订单API集成测试
 */
const request = require('supertest');
const { app, startServer } = require('../src/app');
const { initTestUsers } = require('../src/models/User');
const { initTestOrders } = require('../src/models/Order');

describe('订单API测试', () => {
  let server;
  let adminToken;
  let userToken;

  // 测试前启动服务器并获取Token
  beforeAll(async () => {
    await initTestUsers();
    initTestOrders();
    server = await startServer();

    // 获取管理员Token
    const adminLogin = await request(app)
      .post('/api/auth/login')
      .send({
        username: 'admin',
        password: 'admin123',
      });
    adminToken = adminLogin.body.data.token;

    // 获取普通用户Token
    const userLogin = await request(app)
      .post('/api/auth/login')
      .send({
        username: 'user1',
        password: 'user123',
      });
    userToken = userLogin.body.data.token;
  });

  // 测试后关闭服务器
  afterAll((done) => {
    server.close(done);
  });

  describe('GET /api/orders', () => {
    test('应该返回订单列表', async () => {
      const response = await request(app)
        .get('/api/orders')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(200);
      expect(response.body.success).toBe(true);
      expect(response.body.data.items).toBeDefined();
      expect(response.body.data.pagination).toBeDefined();
      expect(response.body.data.pagination.total).toBeGreaterThan(0);
    });

    test('应该支持分页', async () => {
      const response = await request(app)
        .get('/api/orders?page=1&pageSize=5')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(200);
      expect(response.body.data.items.length).toBeLessThanOrEqual(5);
      expect(response.body.data.pagination.page).toBe(1);
      expect(response.body.data.pagination.pageSize).toBe(5);
    });

    test('应该支持状态筛选', async () => {
      const response = await request(app)
        .get('/api/orders?status=pending')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(200);
      response.body.data.items.forEach((order) => {
        expect(order.status).toBe('pending');
      });
    });

    test('应该支持用户名筛选', async () => {
      const response = await request(app)
        .get('/api/orders?userName=张三')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(200);
      response.body.data.items.forEach((order) => {
        expect(order.userName).toContain('张三');
      });
    });

    test('应该支持订单号筛选', async () => {
      const response = await request(app)
        .get('/api/orders?orderNo=ORD20240101001')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(200);
      expect(response.body.data.items.length).toBeGreaterThan(0);
      response.body.data.items.forEach((order) => {
        expect(order.orderNo).toContain('ORD20240101001');
      });
    });

    test('应该支持金额范围筛选', async () => {
      const response = await request(app)
        .get('/api/orders?minAmount=100&maxAmount=500')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(200);
      response.body.data.items.forEach((order) => {
        expect(order.totalAmount).toBeGreaterThanOrEqual(100);
        expect(order.totalAmount).toBeLessThanOrEqual(500);
      });
    });

    test('应该支持日期范围筛选', async () => {
      const response = await request(app)
        .get('/api/orders?startDate=2024-01-01&endDate=2024-01-05')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(200);
    });

    test('应该支持排序', async () => {
      // 按金额升序
      const responseAsc = await request(app)
        .get('/api/orders?sortBy=totalAmount&sortOrder=asc')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(responseAsc.status).toBe(200);
      const amountsAsc = responseAsc.body.data.items.map((o) => o.totalAmount);
      for (let i = 1; i < amountsAsc.length; i++) {
        expect(amountsAsc[i]).toBeGreaterThanOrEqual(amountsAsc[i - 1]);
      }

      // 按金额降序
      const responseDesc = await request(app)
        .get('/api/orders?sortBy=totalAmount&sortOrder=desc')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(responseDesc.status).toBe(200);
      const amountsDesc = responseDesc.body.data.items.map((o) => o.totalAmount);
      for (let i = 1; i < amountsDesc.length; i++) {
        expect(amountsDesc[i]).toBeLessThanOrEqual(amountsDesc[i - 1]);
      }
    });

    test('应该验证分页参数', async () => {
      const response = await request(app)
        .get('/api/orders?page=0&pageSize=200')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(400);
      expect(response.body.success).toBe(false);
    });

    test('应该验证状态值', async () => {
      const response = await request(app)
        .get('/api/orders?status=invalid_status')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(400);
      expect(response.body.success).toBe(false);
    });

    test('应该拒绝无Token的请求', async () => {
      const response = await request(app).get('/api/orders');

      expect(response.status).toBe(401);
      expect(response.body.success).toBe(false);
    });
  });

  describe('GET /api/orders/:id', () => {
    let orderId;

    beforeAll(async () => {
      // 获取一个订单ID
      const response = await request(app)
        .get('/api/orders?pageSize=1')
        .set('Authorization', `Bearer ${adminToken}`);
      orderId = response.body.data.items[0].id;
    });

    test('应该返回订单详情', async () => {
      const response = await request(app)
        .get(`/api/orders/${orderId}`)
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(200);
      expect(response.body.success).toBe(true);
      expect(response.body.data.order.id).toBe(orderId);
      expect(response.body.data.order.items).toBeDefined();
      expect(response.body.data.order.statusText).toBeDefined();
    });

    test('应该返回404对于不存在的订单', async () => {
      const response = await request(app)
        .get('/api/orders/non-existent-id')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(404);
      expect(response.body.success).toBe(false);
    });
  });

  describe('GET /api/orders/statuses', () => {
    test('应该返回订单状态列表', async () => {
      const response = await request(app)
        .get('/api/orders/statuses')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(response.status).toBe(200);
      expect(response.body.success).toBe(true);
      expect(response.body.data.statuses).toBeDefined();
      expect(response.body.data.statuses.length).toBeGreaterThan(0);
    });
  });

  describe('POST /api/orders', () => {
    test('应该成功创建订单', async () => {
      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          items: [
            {
              productId: 'test-product-1',
              productName: '测试商品',
              quantity: 2,
              price: 99.99,
            },
          ],
          shippingAddress: '测试地址',
        });

      expect(response.status).toBe(201);
      expect(response.body.success).toBe(true);
      expect(response.body.data.order.id).toBeDefined();
      expect(response.body.data.order.orderNo).toBeDefined();
      expect(response.body.data.order.status).toBe('pending');
    });

    test('应该验证必填字段', async () => {
      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          items: [],
        });

      expect(response.status).toBe(400);
      expect(response.body.success).toBe(false);
    });

    test('应该验证商品数量', async () => {
      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          items: [
            {
              productId: 'test-product',
              productName: '测试商品',
              quantity: 0,
              price: 99.99,
            },
          ],
          shippingAddress: '测试地址',
        });

      expect(response.status).toBe(400);
      expect(response.body.success).toBe(false);
    });
  });

  describe('PUT /api/orders/:id/status', () => {
    let orderId;

    beforeAll(async () => {
      // 创建一个测试订单
      const response = await request(app)
        .post('/api/orders')
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          items: [
            {
              productId: 'test-product',
              productName: '测试商品',
              quantity: 1,
              price: 99.99,
            },
          ],
          shippingAddress: '测试地址',
        });
      orderId = response.body.data.order.id;
    });

    test('应该成功更新订单状态', async () => {
      const response = await request(app)
        .put(`/api/orders/${orderId}/status`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          status: 'paid',
        });

      expect(response.status).toBe(200);
      expect(response.body.success).toBe(true);
      expect(response.body.data.order.status).toBe('paid');
    });

    test('应该验证状态值', async () => {
      const response = await request(app)
        .put(`/api/orders/${orderId}/status`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          status: 'invalid_status',
        });

      expect(response.status).toBe(400);
      expect(response.body.success).toBe(false);
    });

    test('应该返回404对于不存在的订单', async () => {
      const response = await request(app)
        .put('/api/orders/non-existent-id/status')
        .set('Authorization', `Bearer ${adminToken}`)
        .send({
          status: 'paid',
        });

      expect(response.status).toBe(404);
      expect(response.body.success).toBe(false);
    });
  });
});