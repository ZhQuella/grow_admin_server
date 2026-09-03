# Grow Admin Server

Grow Admin Server 是 Grow Admin 的 Java 后端服务。项目采用 Gradle 多模块结构，整体分为三层：

- `grow-start`：启动模块，负责组装基础服务和业务模块。
- `grow-base-service`：基础服务部分，提供平台通用能力。
- `grow-business`：业务部分，承载具体业务系统。

## 目录结构

```text
grow_admin_server
├── build.gradle
├── settings.gradle
│
├── grow-start
│   ├── build.gradle
│   └── src/main/java/dev/gad/main
│       └── BackendApplication.java
│
├── grow-base-service
│   ├── base-common
│   ├── base-infra
│   ├── base-security
│   ├── base-account
│   ├── base-access
│   ├── base-org
│   ├── base-designer
│   └── base-runtime
│
└── grow-business
    ├── business-common
    ├── business-workspace
    ├── business-oa
    ├── business-crm
    └── business-demo
```

## 模块职责

### grow-start

启动模块，包含 Spring Boot 启动类。

主要职责：

- 启动整个后端服务。
- 引入基础服务模块。
- 引入业务模块。
- 放置 `application.yaml` 等应用级配置。

### grow-base-service

基础服务部分，放平台级、可复用、与具体业务无关的能力。

| 模块 | 职责 |
|---|---|
| `base-common` | 通用返回、异常、分页、常量、工具类 |
| `base-infra` | 数据库、Redis、文件、审计、日志、导入导出等基础设施 |
| `base-security` | JWT、认证过滤器、权限校验、当前登录用户上下文 |
| `base-account` | 账号、密码、登录记录、账号与人员绑定 |
| `base-access` | 角色、菜单、功能权限、数据权限、账号角色关系 |
| `base-org` | 人员、部门、岗位、职级、任职关系、上下级、人事历史 |
| `base-designer` | 页面设计器、报表设计器、数据库建模、数据准备、数据清洗、流程定义 |
| `base-runtime` | 低代码页面运行时、报表取数、数据集查询、清洗流执行、流程实例 |

### grow-business

业务部分，放具体业务系统。

| 模块 | 职责 |
|---|---|
| `business-common` | 业务通用枚举、DTO、常量、工具类 |
| `business-workspace` | 工作台、首页统计、通知、待办 |
| `business-oa` | OA 业务，如请假、报销、审批 |
| `business-crm` | CRM 业务，如客户、联系人、商机、合同 |
| `business-demo` | 学习和演示用业务模块 |

## 依赖方向

依赖方向必须保持单向：

```text
grow-start
  ├── 依赖 grow-base-service
  └── 依赖 grow-business

grow-business
  └── 可以依赖 grow-base-service

grow-base-service
  └── 不依赖 grow-business
```

简单判断标准：

- 多个业务都可能用到的能力，放进 `grow-base-service`。
- 只属于某个业务场景的代码，放进 `grow-business`。
- 启动、配置、模块装配，放进 `grow-start`。

## Java 包规划

基础服务包名：

```text
dev.gad.common
dev.gad.infra
dev.gad.security
dev.gad.account
dev.gad.access
dev.gad.org
dev.gad.designer
dev.gad.runtime
```

业务包名：

```text
dev.gad.business.common
dev.gad.business.workspace
dev.gad.business.oa
dev.gad.business.crm
dev.gad.business.demo
```

每个具体功能模块内部建议保持统一分层：

```text
controller   // 接口层，接收前端请求
service      // 业务逻辑层
mapper       // 数据库访问层
entity       // 数据库表对象
dto          // 请求参数对象
vo           // 返回结果对象
convert      // 对象转换
enums        // 当前模块枚举
```

## 运行与验证

当前项目使用 Java 17 作为编译目标。

如果本机默认 Java 版本过高，可以临时指定 JDK 17：

```bash
JAVA_HOME=/Users/aaron/Library/Java/JavaVirtualMachines/corretto-17.0.20/Contents/Home ./gradlew test
```

查看 Gradle 模块：

```bash
./gradlew projects
```

编译项目：

```bash
./gradlew compileJava
```

运行测试：

```bash
./gradlew test
```

打包启动模块：

```bash
./gradlew :grow-start:bootJar
```

## 后续开发顺序建议

建议按下面顺序逐步补代码：

1. `base-common`：统一返回 `Result`、业务异常、分页对象。
2. `base-infra`：数据库、Redis、MyBatis、审计日志基础配置。
3. `base-account`：账号实体、登录接口、密码处理。
4. `base-security`：JWT、登录过滤器、当前用户上下文。
5. `base-access`：角色、菜单、功能权限、数据权限。
6. `base-org`：人员、部门、岗位、任职关系。
7. `base-designer`：设计器 JSON Schema 的保存、回显、发布。
8. `base-runtime`：运行时查询、页面取数、流程实例。
9. `grow-business`：具体业务模块。
