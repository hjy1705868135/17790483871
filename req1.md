# 电商购物平台系统 — 项目开发需求

## 1 技术栈

### （1）系统环境
- Java 17
- Servlet 6.0 (Jakarta EE)
- Apache Maven 3

### （2）主框架
- Spring Boot 3.1.x / 3.2.x
- Spring Framework 6.x
- Spring Security 6.x
- JWT (io.jsonwebtoken) 0.11.x

### （3）持久层
- MyBatis Plus 3.5.x
- Hibernate Validation 8.x (Jakarta Bean Validation)
- 支持 SQLite 和 MySQL 数据库，可自由切换

### （4）视图层
- Vue 3.4（Composition API）
- Vite 5.x（构建工具）
- Element Plus 2.4（UI 组件库）
- Pinia 2.x（状态管理）
- Vue Router 4.x（前端路由）

## 2 数据库
- 默认使用 SQLite 数据库开发测试
- 生产环境可切换到 MySQL
- 通过 `application.properties` 切换数据源

## 3 业务功能
- 用户认证：JWT 登录、令牌刷新、登出、获取用户信息
- 订单管理：创建订单、订单列表、订单详情、订单状态更新、订单删除
- 商品管理：商品分页列表、商品详情、分类列表
- 前端页面：首页、登录页、商品列表、商品详情、订单列表、订单详情

## 4 安全设计
- Spring Security + JWT 无状态认证
- BCrypt 密码加密
- 账户锁定机制（5次失败锁定15分钟）
- 角色权限控制（USER / ADMIN）

## 5 架构设计
- 四层架构：Controller → Service → Domain ← Repository
- 统一响应格式 `ApiResponse`（code / message / data）
- 全局异常处理 `GlobalExceptionHandler`
- MyBatis Plus 逻辑删除、自动填充时间戳
- 订单状态机流转控制

## 6 质量保障
- Maven 多 Profile 构建（harness-new / harness-legacy / security-scan）
- Checkstyle 代码风格检查
- SpotBugs 静态代码分析
- JaCoCo 代码覆盖率 ≥ 80%
- OWASP 依赖漏洞扫描
