# Spring Boot Harness 工程管理模板

> **核心定位**：Harness 是部署在工作区根目录的"监管层"。当你在同一工作区内创建任意 Spring Boot 子项目时，Harness 的 Cursor rules、hooks 和规范文档**自动对该子项目生效**，Cursor Agent 在整个研发生命周期中均受 Harness 约束，无需额外配置。

---

## 模板概述

Spring Boot Harness 工程管理模板通过 Cursor IDE 的原生机制（rules、hooks、规范文档）将工程标准内嵌到 AI 辅助开发流程中，实现**规范即代码、门禁即流程**的研发基线。

| 场景 | 模式 | 说明 |
|------|------|------|
| 从零创建新项目 | Bootstrap_Mode | 全量质量门禁，Harness 对新建子项目全流程监管 |
| 历史项目接入 | Retrofit_Mode | 三阶段渐进接入，不破坏存量交付节奏 |

**监管范围**：需求分析 → 技术设计 → 编码实现 → 测试验证 → 文档同步 → CI 发布（六阶段全覆盖）

---

## 目录结构

```
Workspace_Root/                          ← 工作区根目录（Harness 监管层）
├── .cursor/
│   ├── rules/                           ← AI 行为约束文件（自动加载）
│   │   ├── java-engineering-standards.md    # Java 工程规范（四层架构、编码标准）
│   │   ├── testing-quality-standards.md     # 测试与质量规范（分层测试策略）
│   │   ├── api-doc-sync-protocol.md         # API 文档同步规范
│   │   ├── ai-collaboration-protocol.md     # AI 协作协议（Cursor Agent 行为约束）
│   │   └── project-lifecycle.md             # Dev_Lifecycle 六阶段流程
│   ├── hooks.json                       ← Cursor hooks 主配置
│   └── hooks/                           ← 事件驱动自动化触发脚本
│       └── harness-reminder.ps1             # 文件变更后触发分层/测试/文档提醒
├── harness-collab/                      ← AI 协作文档体系
│   ├── README.md
│   ├── AGENTS.md                            # AI 协作协议（完整版）
│   ├── func.md                              # 功能资产总表
│   ├── 01-product-specs/                    # 需求文档（PRD）
│   ├── 02-design-docs/                      # 技术设计文档
│   ├── 03-exec-plans/                       # 执行计划与测试记录
│   ├── 04-api-docs/                         # API 接口文档
│   ├── 05-methodology/                      # 方法论（架构约束、工作流、AI 交付手册）
│   └── 06-adapters/                         # 接入指南（Bootstrap / Retrofit）
├── config/
│   ├── checkstyle/
│   │   ├── checkstyle.xml                   # 默认规则（harness-legacy 使用）
│   │   └── checkstyle-strict.xml            # 严格规则（harness-new 使用）
│   └── spotbugs/
│       └── exclude.xml                      # SpotBugs 误报排除清单
├── .github/
│   └── workflows/
│       └── ci-verify.yml                    # GitHub Actions CI 配置
├── pom.xml                              ← Maven 多 Profile 构建配置
├── README.md                            ← 本文件
├── AGENTS.md                            ← AI 协作协议入口
├── harness-verify.sh                    ← 模板完整性验证脚本
└── {sub-project}/                       ← 开发者新建的 Spring Boot 子项目（自动受监管）
    ├── pom.xml
    └── src/
        ├── main/java/com/example/{project}/
        │   ├── config/
        │   ├── common/
        │   ├── exception/
        │   ├── controller/
        │   ├── service/
        │   ├── domain/
        │   └── repository/
        └── test/java/com/example/{project}/
            └── support/
```

---

## 快速入门

### Bootstrap_Mode：新项目接入（约 30 分钟）

**步骤 1：将 Harness 模板部署到工作区根目录**

```bash
# 验证模板完整性（Linux/macOS）
bash harness-verify.sh

# Windows PowerShell 用户请参考 harness-collab/06-adapters/bootstrap-guide.md
```

**步骤 2：在工作区根目录下创建 Spring Boot 子项目**

```bash
# 使用 Spring Initializr 生成子项目
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.2.0 \
  -d baseDir=my-service \
  -d groupId=com.example \
  -d artifactId=my-service \
  -d javaVersion=21 \
  -d dependencies=web,data-jpa,validation \
  -o my-service.zip && unzip my-service.zip && rm my-service.zip
```

**步骤 3：在 Cursor 中打开工作区根目录**

Cursor 自动加载 `.cursor/rules/` 下所有规则文件，约束立即生效，无需任何额外配置。

**步骤 4：验证规则文件已加载**

```powershell
# 确认 5 个规则文件已存在
(Get-ChildItem ".cursor/rules/*.mdc").Count
# 预期输出：5
```

**步骤 5：开始开发**

向 Cursor Agent 发起任何开发请求，Agent 会自动执行 GATE-01 检查（确认需求文档是否存在），引导你走完整的 Dev_Lifecycle 六阶段流程。

> 详细说明：[harness-collab/06-adapters/bootstrap-guide.md](harness-collab/06-adapters/bootstrap-guide.md)

---

### Retrofit_Mode：历史项目接入（三阶段渐进迁移）

#### 阶段一：observe（仅生成报告，1-2 周）

```bash
mvn clean verify -Pharness-legacy -pl legacy-service
# 效果：生成违规报告，不阻断构建
```

#### 阶段二：warn（警告不阻断，2-4 周）

继续使用 `harness-legacy` profile，新增代码严格遵循 Harness 规范，逐步修复存量违规。

#### 阶段三：enforce（违规阻断构建）

```bash
mvn clean verify -Pharness-new -pl legacy-service
# 效果：Checkstyle 严格规则 + SpotBugs + JaCoCo ≥ 80%，违规阻断构建
```

> 详细说明：[harness-collab/06-adapters/retrofit-guide.md](harness-collab/06-adapters/retrofit-guide.md)

---

## 常用命令参考

| 命令 | 说明 |
|------|------|
| `bash harness-verify.sh` | 验证 Harness 模板完整性 |
| `mvn clean verify -Pharness-new` | 新项目全量质量门禁（违规阻断构建） |
| `mvn clean verify -Pharness-legacy` | 历史项目宽松检查（仅警告） |
| `mvn verify -Psecurity-scan` | OWASP 安全扫描 |

---

## Cursor Agent 使用说明

Cursor Agent 在本工作区内遵循 Dev_Lifecycle 六阶段流程：

1. **需求分析** → 先在 `harness-collab/01-product-specs/` 创建需求文档
2. **技术设计** → 在 `harness-collab/02-design-docs/` 创建设计文档
3. **编码实现** → 严格遵循四层架构，同步生成测试类
4. **测试验证** → 确认覆盖率 ≥ 80%，记录到 `harness-collab/03-exec-plans/`
5. **文档同步** → 更新 `harness-collab/04-api-docs/` 和 `func.md`
6. **CI 发布** → `mvn clean verify -Pharness-new` 全量通过后创建 PR

完整协议：[harness-collab/AGENTS.md](harness-collab/AGENTS.md)

---

## 相关文档

| 文档 | 说明 |
|------|------|
| [AGENTS.md](AGENTS.md) | AI 协作协议入口 |
| [harness-collab/AGENTS.md](harness-collab/AGENTS.md) | AI 协作协议完整版 |
| [harness-collab/05-methodology/dev-workflow.md](harness-collab/05-methodology/dev-workflow.md) | Dev_Lifecycle 六阶段工作流 |
| [harness-collab/05-methodology/architecture-constraints.md](harness-collab/05-methodology/architecture-constraints.md) | 四层架构约束详细说明 |
| [harness-collab/06-adapters/bootstrap-guide.md](harness-collab/06-adapters/bootstrap-guide.md) | 新项目接入详细指南 |
| [harness-collab/06-adapters/retrofit-guide.md](harness-collab/06-adapters/retrofit-guide.md) | 历史项目接入详细指南 |
