# 幼儿园管理系统

> 全栈Web应用 — 课程大作业

## 项目概述

幼儿园管理系统，实现幼儿学籍管理、课程管理、食谱管理、调班管理、考勤管理、数据统计等功能。

- **后端：** Spring Boot 2.7.18 / Java 8 / MySQL 8.0
- **前端：** Vue 3 / Vite / Element Plus
- **部署：** Docker Compose（MySQL + Spring Boot + Nginx）
- **架构：** 前后端分离（REST API）

## 环境要求

### Docker 部署（推荐）
- **Docker Desktop** — 已安装并启动

### 本地开发
- **JDK 8+** — `java -version` 确认可用
- **Maven 3.6+** — `mvn -version` 确认可用
- **Node.js 18+** — `node -v` 确认可用
- **MySQL 8.0+** — 确保MySQL服务已启动

## 快速开始

### Docker Compose 部署（推荐）

```bash
# 1. 克隆仓库
git clone https://github.com/egg-rolls/20260708.git
cd 20260708

# 2. 一键启动
docker compose up -d --build

# 3. 访问系统
# 前端页面：http://localhost
# 后端API：http://localhost:8080
```

### 本地开发

#### 后端开发

```bash
cd backend

# 使用 Maven 构建
mvn clean package -DskipTests

# 运行后端
java -jar target/kindergarten-management-1.0.0.jar
```

#### 前端开发

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

### 旧版控制台程序

```bash
# 编译
javac -encoding UTF-8 -d out -cp "lib/*" src/kindergarten/**/*.java

# 运行
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.Main
```

## 预置账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| teacher01 | 123456 | 教师（大一班） |
| teacher02 | 123456 | 教师（大二班） |
| teacher03 | 123456 | 教师（大三班） |
| teacher04 | 123456 | 教师（中一班） |
| teacher05 | 123456 | 教师（中二班） |
| teacher06 | 123456 | 教师（中三班） |
| teacher07 | 123456 | 教师（小一班） |
| teacher08 | 123456 | 教师（小二班） |
| teacher09 | 123456 | 教师（小三班） |

## 系统功能

### 管理员功能
1. **幼儿学籍管理** — 增删改查、按班级查看、按姓名搜索
2. **课程管理** — 增删课程、选课退课、容量校验
3. **食谱管理** — 菜品库管理、每周排餐、复制上周食谱
4. **调班管理** — 跨年级调班、调班记录
5. **考勤管理** — 查看考勤记录、出勤率统计
6. **数据统计** — 班级人数、课程选课、年级分布、出勤率

### 教师功能
1. 查看本班幼儿
2. 考勤打卡（支持逐个/批量）
3. 查看本班考勤记录
4. 查看课程安排
5. 查看本周食谱

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
├── docker-compose.yml              Docker 编排配置
├── PLAN.md                         开发计划
├── README.md                       本文件
└── src/                            旧版控制台程序源码
```

## API 端点

| 路径 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/children` | GET/POST/PUT/DELETE | 幼儿管理 |
| `/api/classes` | GET | 班级查询 |
| `/api/courses` | GET/POST/PUT/DELETE | 课程管理 |
| `/api/menus` | GET/POST/PUT/DELETE | 食谱管理 |
| `/api/attendance` | GET/POST | 考勤记录 |
| `/api/attendance/batch` | POST | 批量考勤 |
| `/api/transfers` | POST | 调班操作 |
| `/api/statistics/*` | GET | 数据统计 |

## Docker 服务

| 服务 | 镜像 | 端口 | 说明 |
|------|------|------|------|
| mysql | mysql:8.0 | 3306 | MySQL 数据库 |
| backend | kindergarten-backend | 8080 | Spring Boot API |
| frontend | kindergarten-frontend | 80 | Nginx 静态托管 |

## 注意事项

1. 首次运行会自动创建数据库和表，无需手动执行SQL
2. 如果表已存在，不会重复插入预置数据
3. 删除幼儿为软删除（标记离园），不会物理删除记录
4. 每个幼儿最多选4门兴趣课程
5. 调班支持跨年级（如从小班调到中班）
6. Docker 部署时，数据库密码通过环境变量配置

## 相关文档

- [产品需求文档](docs/PRD-产品需求文档.md)
- [产品边界文档](docs/PBD-产品边界文档.md)
- [技术规格文档](docs/SPEC-技术规格文档.md)
- [Docker 部署指南](DOCKER.md)
- [贡献指南](CONTRIBUTING.md)
- [更新日志](CHANGELOG.md)
