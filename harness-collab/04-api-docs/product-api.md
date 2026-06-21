# 商品模块 API 文档

> **关联设计文档**：[商品列表设计文档](../02-design-docs/product-listing-design.md)、[商品详情组件设计文档](../02-design-docs/product-detail-components-design.md)  
> **文档版本**：v1.0  
> **创建时间**：2026-06-17  
> **最后更新**：2026-06-17  
> **负责人**：@dev

---

## 概述

- **基础路径**：`/api/v1/products`
- **API 版本**：v1
- **认证方式**：所有商品接口为公开接口，无需认证
- **内容类型**：`application/json`
- **字符编码**：UTF-8

---

## 接口列表

| 方法 | 路径 | 描述 | 认证 | 引入版本 |
|------|------|------|------|---------|
| GET | `/api/v1/products` | 分页查询商品列表 | 否 | v1.0 |
| GET | `/api/v1/products/{id}` | 获取商品详情 | 否 | v1.0 |
| GET | `/api/v1/products/categories` | 获取所有商品分类 | 否 | v1.0 |

---

## 接口详情

### GET /api/v1/products

**描述**：分页查询商品列表，支持按分类、价格区间筛选和多种排序方式。

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|------|--------|------|
| page | query | Integer | 否 | 1 | 页码 |
| size | query | Integer | 否 | 10 | 每页大小，最大 100 |
| categoryId | query | Long | 否 | — | 商品分类ID |
| minPrice | query | BigDecimal | 否 | — | 最低价格 |
| maxPrice | query | BigDecimal | 否 | — | 最高价格 |
| sortBy | query | String | 否 | createdAt | 排序字段：price / sales / rating / createdAt |
| sortOrder | query | String | 否 | desc | 排序方向：asc / desc |
| keyword | query | String | 否 | — | 关键词搜索（商品名称模糊匹配） |

**请求示例**：

```http
GET /api/v1/products?page=1&size=10&categoryId=1&minPrice=100&maxPrice=1000&sortBy=price&sortOrder=asc HTTP/1.1
Host: api.example.com
```

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "iPhone 15",
        "description": "最新款智能手机",
        "image": "https://example.com/image.jpg",
        "price": 5999.00,
        "originalPrice": 6999.00,
        "categoryId": 1,
        "categoryName": "电子产品",
        "sales": 1000,
        "rating": 4.8,
        "stock": 100,
        "isNew": true,
        "isHot": true,
        "createdAt": "2026-01-01T00:00:00Z"
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
| records | Array | 商品列表 |
| records[].id | Long | 商品ID |
| records[].name | String | 商品名称 |
| records[].description | String | 商品描述 |
| records[].image | String | 商品主图URL |
| records[].price | BigDecimal | 当前售价 |
| records[].originalPrice | BigDecimal | 原价 |
| records[].categoryId | Long | 分类ID |
| records[].categoryName | String | 分类名称 |
| records[].sales | Integer | 累计销量 |
| records[].rating | BigDecimal | 评分（1-5） |
| records[].stock | Integer | 库存数量 |
| records[].isNew | Boolean | 是否新品 |
| records[].isHot | Boolean | 是否热销 |
| records[].createdAt | String | 创建时间（ISO 8601 格式） |
| total | Long | 总记录数 |
| size | Integer | 每页大小 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 查询成功 |
| 400 | 400 | 请求参数错误（如 size 超过最大值） |
| 500 | 500 | 服务器内部错误 |

---

### GET /api/v1/products/{id}

**描述**：根据商品 ID 获取商品详细信息，包含促销信息。

**请求参数**：

| 参数名 | 位置 | 类型 | 必填 | 描述 |
|--------|------|------|------|------|
| id | path | Long | 是 | 商品ID，必须为正整数 |

**请求示例**：

```http
GET /api/v1/products/1 HTTP/1.1
Host: api.example.com
```

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "iPhone 15 Pro",
    "description": "最新款智能手机，A17 Pro 芯片",
    "images": [
      "https://example.com/image1.jpg",
      "https://example.com/image2.jpg"
    ],
    "price": 7999.00,
    "originalPrice": 8999.00,
    "categoryId": 1,
    "categoryName": "电子产品",
    "brand": "Apple",
    "sales": 5000,
    "rating": 4.9,
    "stock": 200,
    "isNew": true,
    "isHot": true,
    "specifications": {
      "颜色": "深空黑色",
      "存储": "256GB",
      "屏幕": "6.1英寸"
    },
    "promotions": [
      {
        "id": 1,
        "type": "LIMITED",
        "text": "限时8折",
        "priority": 10,
        "startTime": "2026-06-16T00:00:00Z",
        "endTime": "2026-06-20T23:59:59Z"
      }
    ],
    "promotionEndTime": "2026-06-20T23:59:59Z",
    "createdAt": "2026-01-01T00:00:00Z",
    "updatedAt": "2026-06-16T10:00:00Z"
  }
}
```

**响应字段说明**：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 商品ID |
| name | String | 商品名称 |
| description | String | 商品描述 |
| images | Array | 商品图片列表 |
| price | BigDecimal | 当前售价 |
| originalPrice | BigDecimal | 原价 |
| categoryId | Long | 分类ID |
| categoryName | String | 分类名称 |
| brand | String | 品牌 |
| sales | Integer | 累计销量 |
| rating | BigDecimal | 评分（1-5） |
| stock | Integer | 库存数量 |
| isNew | Boolean | 是否新品 |
| isHot | Boolean | 是否热销 |
| specifications | Object | 规格参数（键值对） |
| promotions | Array | 促销信息列表 |
| promotions[].id | Long | 促销ID |
| promotions[].type | String | 促销类型：DISCOUNT / LIMITED / NEW / HOT / GIFT |
| promotions[].text | String | 促销文本 |
| promotions[].priority | Integer | 优先级（数值越大越高） |
| promotions[].startTime | String | 开始时间 |
| promotions[].endTime | String | 结束时间 |
| promotionEndTime | String | 最近促销结束时间 |
| createdAt | String | 创建时间 |
| updatedAt | String | 最后更新时间 |

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 查询成功 |
| 404 | 404 | 商品不存在 |
| 500 | 500 | 服务器内部错误 |

---

### GET /api/v1/products/categories

**描述**：获取所有商品分类列表。

**响应示例（200 OK）**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "电子产品",
      "icon": "el-icon-phone",
      "sortOrder": 1,
      "productCount": 120
    },
    {
      "id": 2,
      "name": "服装",
      "icon": "el-icon-suitcase",
      "sortOrder": 2,
      "productCount": 200
    }
  ]
}
```

**响应字段说明**：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 分类ID |
| name | String | 分类名称 |
| icon | String | 分类图标 |
| sortOrder | Integer | 排序顺序 |
| productCount | Integer | 该分类下商品数量 |

**错误码**：

| HTTP 状态码 | 业务码 | 说明 |
|------------|--------|------|
| 200 | 200 | 查询成功 |
| 500 | 500 | 服务器内部错误 |

---

## 错误码汇总

### 通用错误码

| HTTP 状态码 | 业务码 | 说明 | 处理建议 |
|------------|--------|------|----------|
| 400 | 400 | 请求参数错误 | 检查请求参数格式 |
| 404 | 404 | 资源不存在 | 确认资源 ID 是否正确 |
| 500 | 500 | 服务器内部错误 | 联系开发团队排查 |

---

## 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| v1.0 | 2026-06-17 | 初始版本，包含商品列表、商品详情、分类列表接口 | @dev |
