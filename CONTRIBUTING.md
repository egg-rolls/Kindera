# 贡献指南

感谢你对幼儿园管理系统项目的关注！本文档说明如何参与项目开发。

## 开发环境

### 后端开发
- **JDK 8+**
- **Maven 3.6+**
- **MySQL 8.0+**

### 前端开发
- **Node.js 18+**
- **npm 9+**

### Docker 部署
- **Docker Desktop**

## 快速开始

```bash
# 1. 克隆仓库
git clone https://github.com/egg-rolls/20260708.git
cd 20260708

# 2. Docker 一键启动（推荐）
docker compose up -d --build

# 3. 或者本地开发
# 后端
cd backend
mvn clean package -DskipTests
java -jar target/kindergarten-management-1.0.0.jar

# 前端
cd frontend
npm install
npm run dev
```

## 项目结构

```
├── backend/                        Spring Boot 后端
│   ├── src/main/java/kindergarten/
│   │   ├── controller/             REST API 控制器
│   │   ├── service/                业务逻辑层
│   │   ├── dao/                    数据访问层
│   │   ├── entity/                 实体类
│   │   ├── config/                 配置类
│   │   └── util/                   工具类
│   └── pom.xml                     Maven 配置
├── frontend/                       Vue 3 前端
│   ├── src/
│   │   ├── views/                  页面组件
│   │   ├── api/                    API 封装
│   │   └── router/                 路由配置
│   └── package.json                npm 配置
├── docs/                           项目文档
└── docker-compose.yml              Docker 编排配置
```

## 分支策略

```
main          ← 稳定版本，保护分支
  └── dev     ← 开发主线
       ├── feature/xxx   ← 新功能
       ├── fix/xxx       ← Bug 修复
       └── refactor/xxx  ← 重构
```

- **main** — 生产就绪代码，仅通过 PR 合入
- **dev** — 开发集成分支，功能完成后合入
- **feature/** — 新功能开发，从 dev 创建
- **fix/** — Bug 修复，从 dev 创建

## 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

| 类型 | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `refactor` | 重构（不改变功能） |
| `docs` | 文档更新 |
| `style` | 代码格式调整 |
| `test` | 测试相关 |
| `chore` | 构建/工具链变更 |

### 示例

```
feat(child): 新增幼儿信息批量导入功能

- 支持 CSV 格式导入
- 自动校验数据格式
- 重复数据跳过并记录

Closes #12
```

## 代码规范

### 后端（Java）
- 每个类必须有类级别注释（作者、日期、功能、版本）
- 使用 `PreparedStatement`，禁止字符串拼接 SQL
- DAO 层不直接打印输出，通过 `DataAccessException` 传播异常
- 密码使用 `PasswordUtil.hash()` 哈希存储
- Controller 类使用 `@RestController` 和 `@RequestMapping` 注解
- Service 类使用 `@Service` 注解

### 前端（Vue）
- 使用 Vue 3 Composition API
- 组件文件使用 PascalCase 命名
- API 请求统一在 `api/index.js` 中封装
- 使用 Element Plus 组件库

## Pull Request 流程

1. 从 `dev` 创建功能分支
2. 完成开发后推送分支
3. 创建 PR，填写 PR 模板
4. 至少 1 人 Code Review
5. CI 通过后合入 `dev`
6. 版本发布时 `dev` → `main`

## 报告 Bug

使用 [Bug Report](https://github.com/egg-rolls/20260708/issues/new?template=bug_report.md) 模板，包含：

- 复现步骤
- 期望行为 vs 实际行为
- 环境信息（JDK 版本、MySQL 版本、操作系统）

## 开发流程

### 后端开发

1. 在 `backend/src/main/java/kindergarten/` 下开发
2. 遵循分层架构：Controller → Service → DAO → Entity
3. 使用 `@Autowired` 注入依赖
4. 编写单元测试

### 前端开发

1. 在 `frontend/src/` 下开发
2. 页面组件放在 `views/` 目录
3. API 请求封装在 `api/index.js`
4. 使用 Vue Router 管理路由

### Docker 测试

```bash
# 构建并测试
docker compose up -d --build

# 查看日志
docker compose logs -f

# 停止服务
docker compose down
```
