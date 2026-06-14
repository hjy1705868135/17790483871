# 电商购物平台系统

一个完整的电商购物平台系统，包含用户登录认证和订单管理功能。

## 功能特性

### 用户认证
- 基于JWT Token的身份验证机制
- 用户登录/登出功能
- Token自动过期处理
- 安全的密码哈希存储

### 订单管理
- 订单列表查询（支持分页、筛选、排序）
- 订单详情查看
- 订单状态管理
- 多条件筛选（状态、用户、金额、日期）
- 多字段排序（时间、金额、订单号）

## 技术栈

### 后端
- Node.js + Express
- JWT (jsonwebtoken)
- bcryptjs (密码哈希)
- express-validator (参数验证)
- Jest + Supertest (测试)

### 前端
- React 18
- Vite
- React Router
- Axios

## 项目结构

```
ecommerce-platform/
├── backend/                 # 后端服务
│   ├── src/
│   │   ├── config/          # 配置管理
│   │   ├── controllers/     # 业务控制器
│   │   ├── middleware/      # 中间件
│   │   ├── models/          # 数据模型
│   │   ├── routes/          # 路由定义
│   │   ├── utils/           # 工具函数
│   │   └── app.js           # 应用入口
│   ├── tests/               # 测试文件
│   ├── package.json
│   └── .env                 # 环境配置
│
├── frontend/                # 前端应用
│   ├── src/
│   │   ├── components/      # 公共组件
│   │   ├── pages/           # 页面组件
│   │   ├── services/        # API服务
│   │   ├── utils/           # 工具函数
│   │   ├── App.jsx          # 主应用
│   │   └── main.jsx         # 入口文件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── docs/                    # 技术文档
│   ├── authentication.md    # 登录验证原理说明
│   ├── order-query.md       # 订单查询功能说明
│   └── ai-questions.md      # AI提问记录
│
└── README.md                # 项目说明
```

## 快速开始

### 安装依赖

```bash
# 安装后端依赖
cd backend
npm install

# 安装前端依赖
cd frontend
npm install
```

### 启动服务

```bash
# 启动后端服务（端口3001）
cd backend
npm run dev

# 启动前端服务（端口5173）
cd frontend
npm run dev
```

### 运行测试

```bash
cd backend
npm test
```

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| user1 | user123 | 普通用户 |
| user2 | user123 | 普通用户 |

## API接口

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 用户登出 |
| GET | /api/auth/me | 获取当前用户信息 |
| GET | /api/auth/verify | 验证Token有效性 |

### 订单接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/orders | 获取订单列表（支持分页、筛选、排序） |
| GET | /api/orders/:id | 获取订单详情 |
| GET | /api/orders/statuses | 获取订单状态列表 |
| POST | /api/orders | 创建订单 |
| PUT | /api/orders/:id/status | 更新订单状态 |

## 文档说明

- [登录验证原理说明](docs/authentication.md)
- [订单查询功能说明](docs/order-query.md)
- [AI提问记录](docs/ai-questions.md)

## 安全特性

- JWT Token认证
- bcrypt密码哈希
- 参数验证
- CORS配置
- 错误日志记录

## 许可证

MIT License