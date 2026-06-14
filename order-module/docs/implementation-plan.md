# 电商平台订单模块实现思路

## 一、系统架构设计

### 1.1 整体架构
```
┌─────────────────────────────────────────────────────────┐
│                      客户端层                            │
│                  (Web/Mobile/H5)                        │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP/REST API
┌────────────────────────▼────────────────────────────────┐
│                    控制层 (Controller)                    │
│              订单控制器 (OrderController)                 │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                    业务层 (Service)                       │
│           订单服务接口 (OrderService)                      │
│           订单服务实现 (OrderServiceImpl)                  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                    数据访问层 (Mapper)                    │
│          订单Mapper (OrderMapper)                         │
│          订单详情Mapper (OrderItemMapper)                 │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                      数据层                              │
│              MySQL数据库 (订单表、订单详情表)              │
└─────────────────────────────────────────────────────────┘
```

### 1.2 技术选型依据

| 技术栈 | 选型理由 | 版本 |
|--------|----------|------|
| Spring Boot | 快速构建微服务，约定优于配置 | 3.2.0 |
| MyBatis Plus | 简化CRUD操作，支持分页插件 | 3.5.5 |
| MySQL | 关系型数据库，适合订单数据存储 | 8.0 |
| Knife4j | 替代Swagger2，提供更好的API文档体验 | 4.3.0 |
| Lombok | 简化实体类代码，提高开发效率 | - |
| Hutool | 工具库，提供ID生成等实用工具 | 5.8.23 |

## 二、核心业务流程

### 2.1 订单创建流程
```
用户下单请求
    ↓
参数验证（DTO校验）
    ↓
生成订单编号
    ↓
创建订单主记录
    ↓
创建订单详情记录
    ↓
返回订单详情
```

### 2.2 订单状态流转
```
PENDING (待支付)
    ↓ (支付成功)
PAID (已支付)
    ↓ (商家发货)
SHIPPED (已发货)
    ↓ (确认收货)
DELIVERED (已送达)

PENDING (待支付)
    ↓ (用户取消)
CANCELLED (已取消)

PAID (已支付)
    ↓ (退款)
REFUNDED (已退款)
```

### 2.3 订单查询流程
```
查询请求
    ↓
参数验证
    ↓
构建查询条件（筛选、分页、排序）
    ↓
执行数据库查询
    ↓
转换结果为VO
    ↓
返回分页结果
```

## 三、数据流转逻辑

### 3.1 订单数据结构
```
Order (订单主表)
├── id: Long (订单ID)
├── orderNo: String (订单编号)
├── userId: Long (用户ID)
├── userName: String (用户名)
├── totalAmount: BigDecimal (总金额)
├── discountAmount: BigDecimal (优惠金额)
├── freightAmount: BigDecimal (运费)
├── payAmount: BigDecimal (实付金额)
├── status: String (订单状态)
├── receiverInfo (收货信息)
│   ├── receiverName
│   ├── receiverPhone
│   ├── receiverAddress
│   └── receiverPostalCode
├── paymentInfo (支付信息)
│   ├── paymentMethod
│   ├── paymentTime
│   └── paymentNo
├── expressInfo (物流信息)
│   ├── expressCompany
│   ├── expressNo
│   ├── shipTime
│   └── deliveryTime
└── items: List<OrderItem> (订单详情)
    └── OrderItem (订单详情表)
        ├── id
        ├── orderId
        ├── productId
        ├── productName
        ├── skuId
        ├── skuCode
        ├── price
        ├── quantity
        └── subtotal
```

### 3.2 数据流转关系
```
请求参数 (DTO)
    ↓ BeanUtil.copyProperties
实体类 (Entity)
    ↓
数据库表 (MySQL)
    ↓
结果集 (ResultSet)
    ↓ MyBatis Plus自动映射
实体类 (Entity)
    ↓ BeanUtil.copyProperties
视图对象 (VO)
    ↓
响应数据 (JSON)
```

## 四、接口设计原则

### 4.1 RESTful API规范

| 操作 | HTTP方法 | URL模式 | 说明 |
|------|----------|---------|------|
| 创建 | POST | /api/orders | 创建订单 |
| 查询 | GET | /api/orders/{id} | 查询单个订单 |
| 查询 | GET | /api/orders | 分页查询订单列表 |
| 更新 | PUT | /api/orders/{id} | 更新订单信息 |
| 删除 | DELETE | /api/orders/{id} | 删除订单 |
| 状态更新 | PUT | /api/orders/{id}/status | 更新订单状态 |
| 支付 | POST | /api/orders/{id}/pay | 订单支付 |
| 发货 | POST | /api/orders/{id}/ship | 订单发货 |
| 收货 | POST | /api/orders/{id}/delivery | 确认收货 |
| 取消 | POST | /api/orders/{id}/cancel | 取消订单 |

### 4.2 统一响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-10T10:00:00",
  "path": "/api/orders"
}
```

### 4.3 异常处理机制
```java
// 业务异常
throw new BusinessException("订单不存在");

// 验证异常
throw new MethodArgumentNotValidException();

// 全局统一处理
@ExceptionHandler(BusinessException.class)
public Result<Void> handleBusinessException(BusinessException e) {
    return Result.businessError(e);
}
```

## 五、与其他模块的交互关系

### 5.1 模块依赖关系
```
订单模块 (Order Module)
    ├── 依赖：用户模块 (获取用户信息)
    ├── 依赖：商品模块 (获取商品信息)
    ├── 依赖：支付模块 (完成支付回调)
    └── 依赖：库存模块 (扣减库存)
```

### 5.2 接口调用关系
```
订单创建
    ↓ 调用商品模块
验证商品信息
    ↓ 调用库存模块
扣减库存
    ↓ 调用用户模块
验证用户信息
    ↓ 返回订单创建结果
```

### 5.3 消息队列交互（扩展）
```
订单创建成功
    ↓ 发送消息到MQ
库存服务消费
    ↓
扣减库存
    ↓ 发送消息到MQ
订单服务消费
    ↓
更新订单状态
```

## 六、关键设计决策

### 6.1 为什么使用MyBatis Plus？
- 减少样板代码：自动生成CURD方法
- 内置分页插件：无需手动处理分页逻辑
- 逻辑删除支持：一键实现软删除
- 自动填充：简化创建时间、更新时间处理

### 6.2 订单编号生成策略
```java
private String generateOrderNo() {
    return "ORD" + System.currentTimeMillis() + 
           IdUtil.getSnowflakeNextIdStr().substring(16);
}
// 示例：ORD1704858000001
```
- 时间戳保证订单号大致有序
- 雪花算法ID保证全局唯一

### 6.3 事务管理策略
```java
@Transactional(rollbackFor = Exception.class)
public OrderVO createOrder(OrderCreateDTO dto) {
    // 创建订单
    orderMapper.insert(order);
    // 创建订单详情
    orderItemMapper.batchInsert(orderItems);
    // 事务保证原子性
}
```

## 七、数据库设计规范

### 7.1 表设计原则
- 主键使用BIGINT + 雪花算法ID
- 金额字段使用DECIMAL(12,2)
- 状态字段使用VARCHAR
- 必须包含创建时间、更新时间、删除标记
- 必要的索引设计

### 7.2 索引设计
```sql
-- 唯一索引
UNIQUE KEY uk_order_no (order_no)

-- 普通索引
INDEX idx_user_id (user_id)
INDEX idx_status (status)
INDEX idx_created_at (created_at)
INDEX idx_payment_time (payment_time)
```

## 八、性能优化策略

### 8.1 数据库层面
- 添加适当索引
- 使用分页查询
- 避免SELECT *

### 8.2 代码层面
- 使用DTO减少数据传输
- 合理使用缓存
- 批量操作减少数据库交互

### 8.3 查询优化
```java
// 使用分页插件
Page<Order> page = new Page<>(current, size);
IPage<Order> result = orderMapper.selectOrderPage(page, query);

// 限制最大分页大小
if (size > 100) {
    size = 100;
}
```

## 九、安全性考虑

### 9.1 参数验证
- 使用@Valid注解启用参数验证
- DTO层进行参数校验
- 防止SQL注入（MyBatis参数绑定）

### 9.2 业务安全
- 订单状态流转校验
- 订单归属校验
- 防止重复提交

### 9.3 敏感数据
- 密码加密存储
- 敏感字段脱敏
- HTTPS传输

## 十、可扩展性设计

### 10.1 插件化设计
- MyBatis Plus插件机制
- 拦截器统一处理

### 10.2 策略模式
- 订单状态更新策略
- 支付策略

### 10.3 事件驱动
- 订单创建事件
- 订单状态变更事件
