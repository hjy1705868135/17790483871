# 登录验证原理说明

## 一、概述

本系统采用基于JWT（JSON Web Token）的身份验证机制，实现用户登录认证和授权功能。JWT是一种开放标准（RFC 7519），用于在各方之间安全传输信息作为JSON对象。

## 二、JWT Token结构

JWT Token由三部分组成，用点号(.)分隔：

```
Header.Payload.Signature
```

### 1. Header（头部）
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
- `alg`: 签名算法，本系统使用HS256（HMAC SHA-256）
- `typ`: Token类型，固定为JWT

### 2. Payload（载荷）
本系统的Payload包含以下字段：
```json
{
  "userId": "用户唯一标识",
  "username": "用户名",
  "role": "用户角色",
  "iat": "Token签发时间（Unix时间戳）",
  "exp": "Token过期时间（Unix时间戳）"
}
```

### 3. Signature（签名）
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```
签名用于验证Token未被篡改。

## 三、Token生命周期

### 1. Token生成流程

```
用户登录请求 → 验证用户凭据 → 生成JWT Token → 返回Token给客户端
```

**详细步骤：**

1. 用户提交用户名和密码
2. 后端验证用户凭据：
   - 查询用户数据库
   - 使用bcrypt比对密码哈希
3. 验证成功后，生成JWT Token：
   ```javascript
   const token = jwt.sign(
     { userId, username, role },
     JWT_SECRET,
     { expiresIn: '24h' }
   );
   ```
4. 返回Token和用户信息给前端

**代码实现：** [backend/src/utils/jwt.js](file:///c:/Users/69394/Desktop/新建文件夹/backend/src/utils/jwt.js)

### 2. Token传输机制

**请求头格式：**
```
Authorization: Bearer <token>
```

**前端实现：**
- 使用axios请求拦截器自动添加Token
```javascript
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

**代码实现：** [frontend/src/services/api.js](file:///c:/Users/69394/Desktop/新建文件夹/frontend/src/services/api.js)

### 3. Token存储机制

**前端存储：**
- 使用浏览器localStorage存储Token
- 同时存储用户基本信息

```javascript
// 存储Token
localStorage.setItem('token', token);
localStorage.setItem('user', JSON.stringify(user));
```

**安全考虑：**
- localStorage相对于Cookie存储，可避免CSRF攻击
- 但需要注意XSS攻击防护（前端已做输入验证）

**代码实现：** [frontend/src/utils/auth.js](file:///c:/Users/69394/Desktop/新建文件夹/frontend/src/utils/auth.js)

### 4. Token验证机制

**后端验证流程：**

1. 从请求头提取Token
2. 验证Token格式（Bearer格式）
3. 使用jwt.verify验证签名和有效期
4. 解析Payload获取用户信息
5. 注入用户信息到请求对象

```javascript
const decoded = jwt.verify(token, JWT_SECRET);
req.user = { userId, username, role };
```

**验证中间件：** [backend/src/middleware/auth.js](file:///c:/Users/69394/Desktop/新建文件夹/backend/src/middleware/auth.js)

### 5. Token过期处理机制

**过期时间配置：**
- 默认有效期：24小时
- 配置文件：[backend/.env](file:///c:/Users/69394/Desktop/新建文件夹/backend/.env)

**过期处理流程：**

1. Token过期时，jwt.verify抛出TokenExpiredError
2. 后端返回401状态码和错误信息
3. 前端拦截器捕获401错误：
   - 清除本地存储的Token
   - 重定向到登录页面

```javascript
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

## 四、安全措施

### 1. 密码安全
- 使用bcrypt进行密码哈希存储
- 哈希轮数：10（推荐值）
- 原始密码不存储、不传输

### 2. Token安全
- 使用强密钥（建议256位以上）
- Token有效期合理设置（24小时）
- Token签名验证防止篡改

### 3. 传输安全
- 建议生产环境使用HTTPS
- Token通过Authorization头传输（不通过URL）

### 4. 存储安全
- 前端使用localStorage存储
- 注意XSS防护
- 登出时清除存储

## 五、认证流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                        用户登录认证流程                           │
└─────────────────────────────────────────────────────────────────┘

前端                           后端
│                              │
│  1. 输入用户名/密码           │
│  ──────────────────────────> │
│                              │ 2. 验证用户凭据
│                              │    - 查询用户
│                              │    - 比对密码哈希
│                              │
│                              │ 3. 生成JWT Token
│                              │    jwt.sign(payload, secret)
│                              │
│  4. 返回Token + 用户信息      │
│  <────────────────────────── │
│                              │
│  5. 存储Token到localStorage   │
│                              │
│                              │
│  6. 后续请求携带Token         │
│  ──────────────────────────> │
│  Authorization: Bearer Token │
│                              │ 7. 验证Token
│                              │    jwt.verify(token, secret)
│                              │    - 检查签名
│                              │    - 检查有效期
│                              │
│  8. 返回请求结果              │
│  <────────────────────────── │
│                              │
│                              │
│  Token过期                    │
│  ──────────────────────────> │
│                              │ 返回401错误
│  <────────────────────────── │
│                              │
│  9. 清除Token，跳转登录页      │
│                              │
```

## 六、API接口说明

### 登录接口
- **URL**: `POST /api/auth/login`
- **请求体**: `{ username, password }`
- **响应**: `{ success, data: { user, token, expiresIn } }`

### 验证接口
- **URL**: `GET /api/auth/verify`
- **请求头**: `Authorization: Bearer <token>`
- **响应**: `{ success, data: { valid: true, user } }`

### 获取用户信息
- **URL**: `GET /api/auth/me`
- **请求头**: `Authorization: Bearer <token>`
- **响应**: `{ success, data: { user } }`

### 登出接口
- **URL**: `POST /api/auth/logout`
- **请求头**: `Authorization: Bearer <token>`
- **响应**: `{ success, message: '登出成功' }`

## 七、测试账号

系统预置测试账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| user1 | user123 | 普通用户 |
| user2 | user123 | 普通用户 |

## 八、最佳实践建议

1. **生产环境配置**
   - 更换JWT密钥为强随机字符串
   - 启用HTTPS
   - 考虑使用Redis存储Token实现主动失效

2. **安全加固**
   - 实现Token刷新机制
   - 添加登录日志审计
   - 实现IP限制或设备绑定

3. **性能优化**
   - Token验证使用缓存
   - 考虑无状态验证的扩展性