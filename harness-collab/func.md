# 功能资产总表

本文档是项目所有功能的资产登记表，记录每个功能的当前状态、负责人和关联文档。每次功能状态发生变更时，必须同步更新本表。

**最后更新**：2026-06-17

---

## 功能列表

| 功能名称 | 状态 | 负责人 | 需求文档 | 设计文档 | API 文档 | 最后更新 |
|----------|------|--------|----------|----------|----------|----------|
| 用户认证 | 🟢 已交付 | @dev | [用户认证需求](01-product-specs/user-authentication-spec.md) | [用户认证设计](02-design-docs/user-authentication-design.md) | [认证 API](04-api-docs/auth-api.md) | 2026-06-17 |
| 订单管理 | 🟢 已交付 | @dev | [订单管理需求](01-product-specs/order-management-spec.md) | [订单管理设计](02-design-docs/order-management-design.md) | [订单 API](04-api-docs/order-api.md) | 2026-06-17 |
| 商品管理 | 🟢 已交付 | @dev | [商品列表需求](01-product-specs/product-listing-spec.md) | [商品列表设计](02-design-docs/product-listing-design.md) | [商品 API](04-api-docs/product-api.md) | 2026-06-17 |
| 商品详情组件 | 🟢 已交付 | @dev | [商品详情组件需求](01-product-specs/product-detail-components-spec.md) | [商品详情组件设计](02-design-docs/product-detail-components-design.md) | [商品 API](04-api-docs/product-api.md) | 2026-06-17 |
| 前端请求处理 | 🟢 已交付 | @dev | [前端请求处理需求](01-product-specs/frontend-request-handler-spec.md) | [前端请求处理设计](02-design-docs/frontend-request-handler-design.md) | — | 2026-06-17 |

---

## 状态说明

| 状态 | 含义 | 下一步行动 |
|------|------|-----------|
| 🔵 规划中 | 功能已列入计划，尚未开始开发 | 创建需求文档，进入需求分析阶段 |
| 🟡 开发中 | 功能正在开发，代码尚未完成 | 完成编码实现，进入测试验证阶段 |
| 🟠 测试中 | 代码已完成，正在进行测试验证 | 完成测试，更新执行计划，进入文档同步阶段 |
| 🟢 已交付 | 功能已完成所有阶段，CI 已通过 | 无（持续维护） |
| ⚫ 已废弃 | 功能已下线或不再维护 | 归档相关文档 |

---

## 使用说明

### 新增功能记录

当开始一个新功能时，在表格中新增一行：

```markdown
| {功能名称} | 🔵 规划中 | @{负责人} | — | — | — | {今日日期} |
```

### 更新功能状态

随着功能推进，逐步填充文档链接并更新状态：

1. **需求分析完成**：填写需求文档链接，状态改为 `🟡 开发中`
2. **设计完成**：填写设计文档链接
3. **测试开始**：状态改为 `🟠 测试中`
4. **文档同步完成**：填写 API 文档链接
5. **CI 通过**：状态改为 `🟢 已交付`，更新最后更新日期

### 文档路径格式

文档链接使用相对路径，格式为：

- 需求文档：`01-product-specs/{功能英文名}-spec.md`
- 设计文档：`02-design-docs/{功能英文名}-design.md`
- API 文档：`04-api-docs/{模块英文名}-api.md`

如功能尚无对应文档，填写 `—`（破折号）。
