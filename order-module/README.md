# 电商平台订单模块

基于 Spring Boot 3 与 MyBatis Plus 框架的电商平台订单模块，提供完整的订单 CRUD 接口。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 应用框架 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0 | 数据库 |
| Knife4j | 4.3.0 | API文档 |
| Lombok | - | 代码简化 |
| Hutool | 5.8.23 | 工具库 |

## 功能特性

- ✅ 订单创建、查询、更新、删除
- ✅ 订单分页查询（支持多条件筛选）
- ✅ 订单状态流转（支付、发货、收货、取消）
- ✅ 订单详情管理
- ✅ 统一响应格式
- ✅ 全局异常处理
- ✅ API文档自动生成
- ✅ 完整的单元测试和集成测试

## 项目结构

```
order-module/
├── src/
│   ├── main/
│   │   ├── java/com/example/order/
│   │   │   ├── config/         # 配置类
│   │   │   ├── controller/     # 控制器
│   │   │   ├── service/        # 服务层
│   │   │   ├── mapper/         # 数据访问层
│   │   │   ├── entity/         # 实体类
│   │   │   ├── dto/            # 数据传输对象
│   │   │   ├── vo/             # 视图对象
│   │   │   └── exception/      # 异常处理
│   │   └── resources/
│   │       ├── mapper/         # Mapper XML
│   │       ├── schema.sql      # 数据库脚本
│   │       └── application.yml # 配置文件
│   └── test/                   # 测试代码
├── docs/                       # 技术文档
├── pom.xml                     # Maven配置
└── README.md                   # 项目说明
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.9+
- MySQL 8.0+

### 2. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行SQL脚本
source src/main/resources/schema.sql
```

### 3. 配置数据库连接

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce
    username: root
    password: your_password
```

### 4. 编译运行

```bash
# 编译项目
mvn clean install

# 运行项目
mvn spring-boot:run

# 或直接运行jar
java -jar target/order-module-1.0.0.jar
```

### 5. 访问API文档

启动成功后访问：http://localhost:8080/doc.html

## API接口

### 订单管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/orders | 创建订单 |
| GET | /api/orders/{id} | 查询订单详情 |
| GET | /api/orders | 分页查询订单 |
| PUT | /api/orders/{id} | 更新订单 |
| DELETE | /api/orders/{id} | 删除订单 |

### 订单操作

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/orders/{id}/pay | 支付订单 |
| POST | /api/orders/{id}/ship | 发货 |
| POST | /api/orders/{id}/delivery | 确认收货 |
| POST | /api/orders/{id}/cancel | 取消订单 |

## 接口示例

### 创建订单

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1001,
    "userName": "张三",
    "totalAmount": 299.00,
    "payAmount": 289.00,
    "receiverName": "张三",
    "receiverPhone": "13800138001",
    "receiverAddress": "北京市朝阳区XX街道XX号",
    "items": [{
      "productId": 10001,
      "productName": "iPhone 15",
      "price": 7999.00,
      "quantity": 1
    }]
  }'
```

### 查询订单列表

```bash
curl -X GET "http://localhost:8080/api/orders?current=1&size=10&status=PENDING"
```

### 支付订单

```bash
curl -X POST "http://localhost:8080/api/orders/1/pay?paymentMethod=ALIPAY&paymentNo=ZF001"
```

## 测试

```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest=OrderServiceTest

# 运行集成测试
mvn test -Dtest=OrderControllerTest
```

## 订单状态流转

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
```

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-10T10:00:00",
  "path": "/api/orders"
}
```

## 技术文档

- [实现思路说明](docs/implementation-plan.md)
- [测试报告](docs/test-report.md)
- [AI提问记录](docs/ai-questions.md)

## License

MIT License
