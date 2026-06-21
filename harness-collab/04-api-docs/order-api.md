# 订单管理模块 API 文档

> **关联设计文档**：[订单管理设计文档](../02-design-docs/order-management-design.md)  
> **文档版本**：v1.0  
> **创建时间**：2026-06-17  
> **最后更新**：2026-06-17  
> **负责人**：@dev

---

## 概述

- **基础路径**：`/api/v1/orders`
- **API 版本**：v1
- **认证方式**：Bearer Token（所有接口均需认证，部分接口需要 ADMIN 角色）
- **内容类型**：`application/json`
- **字符编码**：UTF-8

---

## 接口列表

| 方法 | 路径 | 描述 | 认证 | 角色 | 引入版本 |
|------|------|------|------|------|---------|
| POST | `/api/v1/orders` | 创建新订单 | 需要 | USER | v1.0 |
| GET | `/api/v1/orders/{id}` | 获取订单详情 | 需要 | USER | v1.0 |
| GET | `/api/v1/orders` | 分页查询订单列表 | 需要 | USER | v1.0 |
| PUT | `/api/v1/orders/{id}/status` | 更新订单状态 | 需要 | ADMIN | v1.0 |
| DELETE | `/api/v1/orders/{id}` | 删除订单（逻辑删除） | 需要 | ADMIN | v1.0 |

---

## 接口详情

### POST /api/v1/orders

**描述**：创建新订单，包含库存校验与扣减，整个操作在事务中完成。

**请求头**：

| 参数名 | 必填 | 说明 |
|--------|------|------|
| Authorization | 是 | Bearer Token |
| Content-Type | 是 | `application/json` |

**请求体**：

```json
{
  "shippingAddress": {
    "receiverName": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detailAddress": "某某街道123号",
    "postalCode": "100000"
  },
  "paymentMethod": "ALIPAY",
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 99.00
    }
  ],
  "remark": "请尽快发货"
}
```

**请求体字段说明**：

| 字段名 | 类型 | 必填 | 校验规则 | 说明 |
|--------|------|------|----------|------|
| shippingAddress | Object | 是 | @Valid | 收货地址信息 |
| shippingAddress.receiverName | String | 是 | @NotBlank | 收货人姓名 |
| shippingAddress.phone | String | 是 | @NotBlank | 收货人电话 |
| shippingAddress.province | String | 是 | @NotBlank | 省 |
| shippingAddress.city | String | 是 | @NotBlank | 市 |
| shippingAddress.district | String | 是 | @NotBlank | 区 |
| shippingAddress.detailAddress | String | 是 | @NotBlank | 详细地址 |
| shippingAddress.postalCode | String | 否 | — | 邮政编码 |
| paymentMethod | String | 是 | @NotBlank | 支付方式：ALIPAY / WECHAT / UNIONPAY |
| items | Array | 是 | @NotEmpty, @Size(min=1) | 订单商品列表 |
| items[].productId | Long | 是 | @NotNull | 商品ID |
| items[].quantity | Integer | 是 | @Min(1) | 购买数量 |
| items[].price | BigDecimal | 是 | @NotNull | 商品单价 |
| remark | String | 否 | @Size(max=500) | 订单备注 |

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "id": 1,
    "orderNo": "ORD202606170001",
    "totalAmount": 198.00,
    "status": "PENDING_PAYMENT",
    "paymentMethod": "ALIPAY",
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "商品A",
        "productImage": "https://example.com/img.jpg",
        "quantity": 2,
        "price": 99.00,
        "subtotal": 198.00
      }
    ],
    "shippingAddress": {
      "receiverName": "张三",
      "phone": "138****8000",
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "detailAddress": "某某街道123号",
      "postalCode": "100000"
    },
    "statusHistory": [
      {
        "status": "PENDING_PAYMENT",
        "remark": "订单创建",
        "createdAt": "2026-06-17T10:00:00Z"
      }
    ],
    "createdAt": "2026-06-17T10:00:00Z",
    "updatedAt": "2026-06-17T10:00:00Z"
  }
}
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 订单创建成功 |
| 400 | 400 | 请求参数校验失败 |
| 401 | 401 | Token 无效或已过期 |
| 409 | 409 | 库存不足 |
| 500 | 500 | 服务器内部错误 |

---

### GET /api/v1/orders/{id}

**描述**：根据订单 ID 查询订单详情。普通用户只能查看自己的订单，管理员可查看所有订单。

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 描述 |
|--------|------|------|------|------|
| id | path | Long | 是 | 订单ID |
| Authorization | header | String | 是 | Bearer Token |

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "orderNo": "ORD202606170001",
    "totalAmount": 198.00,
    "status": "PENDING_PAYMENT",
    "paymentMethod": "ALIPAY",
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "商品A",
        "productImage": "https://example.com/img.jpg",
        "quantity": 2,
        "price": 99.00,
        "subtotal": 198.00
      }
    ],
    "shippingAddress": {
      "receiverName": "张三",
      "phone": "138****8000",
      "province": "北京市",
      "city": "北京市",
      "district": "朝阳区",
      "detailAddress": "某某街道123号",
      "postalCode": "100000"
    },
    "statusHistory": [
      {
        "status": "PENDING_PAYMENT",
        "remark": "订单创建",
        "createdAt": "2026-06-17T10:00:00Z"
      }
    ],
    "createdAt": "2026-06-17T10:00:00Z",
    "updatedAt": "2026-06-17T10:00:00Z"
  }
}
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 查询成功 |
| 401 | 401 | Token 无效或已过期 |
| 403 | 403 | 无权查看该订单 |
| 404 | 404 | 订单不存在 |
| 500 | 500 | 服务器内部错误 |

---

### GET /api/v1/orders

**描述**：分页查询当前用户的订单列表，支持按状态筛选和排序。管理员可查看所有订单。

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|------|--------|------|
| page | query | Integer | 否 | 1 | 页码 |
| size | query | Integer | 否 | 10 | 每页大小，最大 100 |
| status | query | String | 否 | — | 订单状态筛选 |
| sortBy | query | String | 否 | createdAt | 排序字段 |
| sortOrder | query | String | 否 | desc | 排序方向：asc / desc |
| Authorization | header | String | 是 | — | Bearer Token |

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "ORD202606170001",
        "totalAmount": 198.00,
        "status": "PENDING_PAYMENT",
        "itemCount": 2,
        "createdAt": "2026-06-17T10:00:00Z"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

**响应字段说明**：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 订单列表 |
| records[].id | Long | 订单ID |
| records[].orderNo | String | 订单号 |
| records[].totalAmount | BigDecimal | 订单总金额 |
| records[].status | String | 订单状态 |
| records[].itemCount | Integer | 商品数量 |
| records[].createdAt | String | 创建时间 |
| total | Long | 总记录数 |
| size | Integer | 每页大小 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**订单状态枚举**：

| 状态值 | 说明 |
|--------|------|
| PENDING_PAYMENT | 待支付 |
| PAID | 已支付 |
| PROCESSING | 处理中 |
| SHIPPED | 已发货 |
| DELIVERED | 已送达 |
| COMPLETED | 已完成 |
| CANCELLED | 已取消 |
| REFUNDING | 退款中 |
| REFUNDED | 已退款 |

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 查询成功 |
| 400 | 400 | 请求参数错误 |
| 401 | 401 | Token 无效或已过期 |
| 500 | 500 | 服务器内部错误 |

---

### PUT /api/v1/orders/{id}/status

**描述**：更新订单状态（仅管理员可操作）。支持状态机校验，只能按规定的流转路径更新。

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 描述 |
|--------|------|------|------|------|
| id | path | Long | 是 | 订单ID |
| Authorization | header | String | 是 | Bearer Token（需要 ADMIN 角色） |

**请求体**：

```json
{
  "status": "PAID",
  "remark": "用户已完成支付"
}
```

**请求体字段说明**：

| 字段名 | 类型 | 必填 | 校验规则 | 说明 |
|--------|------|------|----------|------|
| status | String | 是 | @NotBlank | 目标订单状态 |
| remark | String | 否 | — | 状态变更备注 |

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "订单状态更新成功",
  "data": {
    "id": 1,
    "orderNo": "ORD202606170001",
    "status": "PAID",
    "statusHistory": [
      {
        "status": "PENDING_PAYMENT",
        "remark": "订单创建",
        "createdAt": "2026-06-17T10:00:00Z"
      },
      {
        "status": "PAID",
        "remark": "用户已完成支付",
        "operator": "admin",
        "createdAt": "2026-06-17T10:05:00Z"
      }
    ]
  }
}
```

**状态流转规则**：

```
PENDING_PAYMENT → PAID / CANCELLED
PAID → PROCESSING / REFUNDING
PROCESSING → SHIPPED
SHIPPED → DELIVERED
DELIVERED → COMPLETED
REFUNDING → REFUNDED
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 状态更新成功 |
| 400 | 400 | 无效的状态流转 |
| 401 | 401 | Token 无效或已过期 |
| 403 | 403 | 无管理员权限 |
| 404 | 404 | 订单不存在 |
| 500 | 500 | 服务器内部错误 |

---

### DELETE /api/v1/orders/{id}

**描述**：删除订单（逻辑删除，仅管理员可操作）。

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 描述 |
|--------|------|------|------|------|
| id | path | Long | 是 | 订单ID |
| Authorization | header | String | 是 | Bearer Token（需要 ADMIN 角色） |

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 删除成功 |
| 401 | 401 | Token 无效或已过期 |
| 403 | 403 | 无管理员权限 |
| 404 | 404 | 订单不存在 |
| 500 | 500 | 服务器内部错误 |

---

## 错误码汇总

### 模块特定错误码

| 业务码 | 说明 | 处理建议 |
|--------|------|----------|
| 409 | 库存不足 | 提示用户商品库存不足，建议减少购买数量 |
| 400 | 无效的状态流转 | 检查状态机规则，确保按正确路径更新 |

---

## 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| v1.0 | 2026-06-17 | 初始版本，包含订单 CRUD 接口和状态管理 | @dev |
