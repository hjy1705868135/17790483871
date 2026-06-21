# 认证模块 API 文档

> **关联设计文档**：[用户认证设计文档](../02-design-docs/user-authentication-design.md)  
> **文档版本**：v1.0  
> **创建时间**：2026-06-17  
> **最后更新**：2026-06-17  
> **负责人**：@dev

---

## 概述

- **基础路径**：`/api/v1/auth`
- **API 版本**：v1
- **认证方式**：Bearer Token（登录/刷新接口除外，在请求头中携带 `Authorization: Bearer {token}`）
- **内容类型**：`application/json`
- **字符编码**：UTF-8

---

## 接口列表

| 方法 | 路径 | 描述 | 认证 | 引入版本 |
|------|------|------|------|---------|
| POST | `/api/v1/auth/login` | 用户登录 | 否 | v1.0 |
| POST | `/api/v1/auth/refresh` | 刷新访问令牌 | 否 | v1.0 |
| POST | `/api/v1/auth/logout` | 用户登出 | 需要 | v1.0 |
| GET | `/api/v1/auth/me` | 获取当前用户信息 | 需要 | v1.0 |

---

## 接口详情

### POST /api/v1/auth/login

**描述**：用户使用用户名和密码登录，成功返回访问令牌和刷新令牌。连续 5 次登录失败后账户将被锁定 15 分钟。

**请求体**：

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**请求体字段说明**：

| 字段名 | 类型 | 必填 | 校验规则 | 说明 |
|--------|------|------|----------|------|
| username | String | 是 | @NotBlank | 用户名 |
| password | String | 是 | @NotBlank | 密码 |

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 1800
  }
}
```

**响应字段说明**：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| accessToken | String | 访问令牌，有效期 30 分钟 |
| refreshToken | String | 刷新令牌，有效期 7 天 |
| tokenType | String | 令牌类型，固定为 "Bearer" |
| expiresIn | Long | 访问令牌过期时间（秒） |

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 登录成功 |
| 400 | 400 | 用户名或密码为空 |
| 401 | 401 | 用户名或密码错误 |
| 423 | 423 | 账户已锁定（5次失败后锁定15分钟） |
| 500 | 500 | 服务器内部错误 |

---

### POST /api/v1/auth/refresh

**描述**：使用刷新令牌获取新的访问令牌。

**请求体**：

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**请求体字段说明**：

| 字段名 | 类型 | 必填 | 校验规则 | 说明 |
|--------|------|------|----------|------|
| refreshToken | String | 是 | @NotBlank | 有效的刷新令牌 |

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "令牌刷新成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 1800
  }
}
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 刷新成功 |
| 400 | 400 | 刷新令牌为空 |
| 401 | 401 | 刷新令牌无效或已过期 |
| 500 | 500 | 服务器内部错误 |

---

### POST /api/v1/auth/logout

**描述**：用户登出，使当前刷新令牌失效。

**请求头**：

| 参数名 | 必填 | 说明 |
|--------|------|------|
| Authorization | 是 | Bearer Token |
| X-Refresh-Token | 是 | 需要失效的刷新令牌 |

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "登出成功",
  "data": null
}
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 登出成功 |
| 401 | 401 | 访问令牌无效 |
| 500 | 500 | 服务器内部错误 |

---

### GET /api/v1/auth/me

**描述**：获取当前登录用户的基本信息。

**请求头**：

| 参数名 | 必填 | 说明 |
|--------|------|------|
| Authorization | 是 | Bearer Token |

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@example.com",
    "phone": "13800138000",
    "avatar": "https://example.com/avatar.jpg",
    "role": "ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-01-01T00:00:00Z"
  }
}
```

**响应字段说明**：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 用户ID |
| username | String | 用户名 |
| nickname | String | 昵称 |
| email | String | 邮箱 |
| phone | String | 手机号 |
| avatar | String | 头像URL |
| role | String | 角色：ADMIN / USER |
| status | String | 状态：ACTIVE / LOCKED / DISABLED |
| createdAt | String | 创建时间（ISO 8601 格式） |

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 查询成功 |
| 401 | 401 | Token 无效或已过期 |
| 404 | 404 | 用户不存在 |
| 500 | 500 | 服务器内部错误 |

---

## 错误码汇总

### 通用错误码

| HTTP 状态码 | 业务码 | 说明 | 处理建议 |
|------------|--------|------|----------|
| 400 | 400 | 请求参数错误 | 检查请求参数格式和必填项 |
| 401 | 401 | 未认证 | 重新登录获取新 Token |
| 404 | 404 | 资源不存在 | 确认资源 ID 是否正确 |
| 500 | 500 | 服务器内部错误 | 联系开发团队排查 |

### 模块特定错误码

| 业务码 | 说明 | 处理建议 |
|--------|------|----------|
| 423 | 账户已锁定 | 等待 15 分钟后重试，或联系管理员解锁 |

---

## 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| v1.0 | 2026-06-17 | 初始版本，包含登录、刷新令牌、登出、获取用户信息接口 | @dev |
