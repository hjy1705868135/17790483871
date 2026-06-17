# SKILL.md — 项目开发规范

本文件定义本项目（cupro 无人机信息管理系统）的开发规范和约束，所有开发者必须遵守。

---

## 一、技术栈要求

### 1.1 系统环境
| 组件 | 版本要求 |
|------|----------|
| Java | Java EE 8（JDK 1.8） |
| Servlet | Servlet 3.0（由 Spring Boot 嵌入式容器提供） |
| Maven | Apache Maven 3 |

### 1.2 主框架
| 组件 | 版本要求 | 说明 |
|------|----------|------|
| Spring Boot | 2.2.13.RELEASE | 主框架 |
| Spring Framework | 5.2.x | 随 Spring Boot 2.2 自动引入 |
| Apache Shiro | 1.7.1 | 安全认证与授权 |

### 1.3 持久层
| 组件 | 版本要求 | 说明 |
|------|----------|------|
| MyBatis Spring Boot Starter | 2.1.4 | MyBatis 3.5.x 集成 |
| Hibernate Validation | 6.0.x | 通过 spring-boot-starter-validation 引入 |
| Alibaba Druid | 1.2.20 | 数据库连接池 |

### 1.4 视图层
| 组件 | 版本要求 | 说明 |
|------|----------|------|
| Thymeleaf | 3.0.x | 服务端模板引擎 |
| Bootstrap | 3.3.7 | 前端UI框架（Webjars） |

---

## 二、数据库要求

- 支持 **SQLite** 和 **MySQL** 两种数据库
- 通过 `application.properties` 中的 `app.db.type` 配置切换（`sqlite` / `mysql`）
- 初期版本默认使用 **SQLite**
- 数据库文件路径：`./data/cupro.db`

---

## 三、四层架构规范

### 3.1 分层结构
```
controller/          → 控制器层（仅依赖 Service 接口）
service/             → 服务层接口
service/impl/        → 服务层实现（接口与实现分包）
repository/          → 数据操作层（MyBatis Mapper 接口）
domain/              → 领域模型（实体、表单、DTO、VO）
config/              → 配置类（数据源、Shiro、WebMVC、MyBatis等）
interceptor/         → 拦截器（独立包，请求日志等）
exception/           → 异常处理（全局异常处理、自定义异常）
```

### 3.2 依赖规则
| 层级 | 允许依赖 | 禁止依赖 |
|------|----------|----------|
| controller | service（接口） | repository、domain 直接操作 |
| service | domain、repository | controller |
| domain | 无（纯 POJO） | 所有业务层 |
| repository | domain | controller、service |
| config | 所有层 | — |
| interceptor | — | controller、service、repository |

### 3.3 禁止行为
- ❌ Controller 直接注入 Mapper/Repository
- ❌ Domain 对象包含 Spring 注解（`@Service`、`@Repository` 等）
- ❌ 跨层调用
- ❌ Service 层直接返回 HTTP 状态码

---

## 四、拦截器规范

- 所有拦截器必须放在独立的 `interceptor/` 包中
- 拦截器必须打印请求信息（方法、URL、参数、IP、耗时、状态码）
- 拦截器通过 `WebMvcConfig` 注册，排除静态资源路径
- 当前已实现：
  - `RequestLogInterceptor`：详细日志拦截器（方法、URL、参数、请求体参数、耗时）
  - `RequestLoggingInterceptor`：简要日志拦截器（含Shiro用户信息）

---

## 五、业务功能规范

### 5.1 无人机信息管理
- 无人机信息录入（支持AI自动生成属性）
- 无人机信息查询（分页、条件搜索）
- 无人机信息删除
- 无人机信息修改
- 无人机属性由 AI 自动生成（通过 `UavAiAttributeGenerator` 接口）

### 5.2 无人机属性清单
| 属性 | 类型 | 说明 |
|------|------|------|
| code | String | 编号（唯一） |
| model | String | 型号 |
| manufacturer | String | 厂商 |
| maxFlightTimeMinutes | Integer | 最大续航（分钟） |
| maxRangeKm | Integer | 最大航程（km） |
| payloadKg | Integer | 载重（kg） |
| maxAltitudeMeters | Integer | 最大飞行高度（米） |
| maxSpeedKmh | Integer | 最大飞行速度（km/h） |
| batteryCapacityMah | Integer | 电池容量（mAh） |
| weightKg | Double | 机身重量（kg） |
| cameraResolution | String | 摄像头分辨率 |
| hasGps | Boolean | 是否有GPS |
| hasObstacleAvoidance | Boolean | 是否有避障功能 |
| purchaseDate | LocalDate | 购买日期 |
| warrantyExpireDate | LocalDate | 保修到期日期 |
| serialNumber | String | 序列号 |
| department | String | 所属部门 |
| applicationField | String | 应用领域 |
| status | String | 状态（ACTIVE/INACTIVE/MAINTENANCE） |
| remark | String | 备注 |

---

## 六、代码规范

### 6.1 命名规范
- 接口命名：`I{Name}Service`（如 `IUavService`）
- 实现类命名：`{Name}ServiceImpl`（如 `UavServiceImpl`）
- Mapper 接口命名：`{Name}Mapper`（如 `UavMapper`）
- Controller 命名：`{Name}Controller`（如 `UavController`）
- 包名：全部小写，使用点分隔

### 6.2 注解使用
- 配置类使用 `@Configuration` + Java 注解配置（优先于 XML）
- 服务实现使用 `@Service`
- Controller 使用 `@Controller`（页面）或 `@RestController`（API）
- Mapper 使用 `@Mapper`
- 参数校验使用 `@Valid` + Hibernate Validation 注解

### 6.3 日志规范
- 使用 SLF4J + Logback
- Controller 层使用 `logger.info` 记录请求
- Service 层使用 `logger.debug`/`logger.info` 记录业务操作
- 拦截器使用 `logger.info` 打印请求信息和耗时

---

## 七、开发流程

遵循 Harness Dev_Lifecycle 六阶段：
1. 需求分析 → 创建需求文档（`harness-collab/01-product-specs/`）
2. 技术设计 → 创建设计文档（`harness-collab/02-design-docs/`）
3. 编码实现 → 严格四层架构 + 测试同步生成
4. 测试验证 → `mvn clean verify -Pharness-new` 覆盖率 ≥ 80%
5. 文档同步 → 更新 API 文档 + `func.md`
6. CI 发布 → PR 合并

---

## 八、快速启动

```bash
# 进入项目目录
cd baseplatform

# 使用默认SQLite数据库启动
mvn spring-boot:run

# 访问地址
# 页面管理：http://localhost:8088/uav
# REST API：http://localhost:8088/api/uav/list
# REST API（旧版）：http://localhost:8088/api/drones

# 切换到MySQL
# 修改 application.properties: app.db.type=mysql
```

---

**最后更新**：2026-06-17
