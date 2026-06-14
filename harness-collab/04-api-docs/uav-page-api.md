# 无人机管理（页面端点）

本模块以 **Thymeleaf 服务端页面** 为主，未提供独立 REST JSON API。以下路径均需 **Shiro 登录** 后访问（匿名：`/login`、`/logout`、静态资源）。

**认证**：默认账号 `admin` / `admin123`。

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/uav` | 分页列表与条件查询（`keyword`、`status`、`pageNum`、`pageSize`） |
| GET | `/uav/add` | 新增表单 |
| POST | `/uav` | 提交新增 |
| GET | `/uav/detail/{id}` | 详情 |
| GET | `/uav/edit/{id}` | 编辑表单 |
| POST | `/uav/update` | 提交更新 |
| POST | `/uav/remove/{id}` | 删除 |

**文档同步**：已同步（与 `baseplatform` 页面路由一致）。
