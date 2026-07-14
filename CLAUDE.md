# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

幼儿园管理系统 — 全栈 Web 应用（课程大作业）。后端 Spring Boot REST API，前端 Vue 3 + Vite，Docker Compose 一键部署。管理幼儿学籍、课程、食谱、调班、考勤和数据统计。

## 技术栈

- **后端**：Spring Boot 2.7.18 / Java 8 / MySQL 8.0 / JDBC（无 ORM）
- **前端**：Vue 3 / Vite / Element Plus / Vue Router / Axios
- **部署**：Docker Compose（MySQL + Spring Boot + Nginx）

## 构建与运行命令

### Docker Compose（推荐）

```bash
docker compose up --build        # 构建并启动全部服务
docker compose down              # 停止并清理容器
docker compose logs -f backend   # 查看后端日志
```

### 后端本地开发

```bash
cd backend
mvn clean package -DskipTests     # 打包
java -jar target/*.jar            # 运行（需本地 MySQL 3306）
```

### 前端本地开发

```bash
cd frontend
npm install
npm run dev                       # 开发服务器 localhost:5173
npm run build                     # 生产构建
```

### 旧版控制台程序（legacy）

```bash
javac -encoding UTF-8 -d out -cp "lib/*" src/kindergarten/**/*.java
java -Xmx256m -Xms64m -cp "out;lib/*" kindergarten.Main
```

## 架构

### 后端分层：Controller → Service → DAO → Entity

- **controller/** — REST API 层，8 个控制器对应各业务模块
- **service/** — 业务逻辑层，带 `@Service` 注解。TransferService 包含事务管理
- **dao/** — 数据访问层，直接 JDBC。每个 DAO 通过 `DBUtil.getConnection()` 获取连接
- **entity/** — 纯数据类（9 个实体：User, ClassInfo, Child, Course, ChildCourse, Dish, WeeklyMenu, Attendance, TransferLog）
- **config/** — `CorsConfig`（跨域配置）、`WebConfig`（静态资源）
- **util/** — `DBUtil`（数据库连接）、`PasswordUtil`（密码工具）

### 前端结构

- `views/` — 页面组件（Login, Dashboard, Children, Courses, Menus, Attendance, Transfers）
- `api/index.js` — Axios 封装，统一请求后端 `/api/*`
- `router/index.js` — Vue Router 路由配置

### Docker 部署

- **mysql:8.0** — 端口 3306，数据卷 `mysql-data`
- **backend** — Spring Boot JAR，端口 8080，依赖 MySQL 健康检查
- **frontend** — Nginx 静态托管，端口 80，反向代理 `/api` 到后端

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

## 关键设计决策

- **数据库自动初始化**：首次运行时 `InitDatabase` 自动创建 `kindergarten` 数据库、9 张表并插入预置数据。使用 `CREATE IF NOT EXISTS`，已有数据时跳过
- **软删除**：幼儿删除为逻辑删除（`status` 字段标记离园），不物理删除
- **数据库配置**：通过环境变量 `SPRING_DATASOURCE_URL` / `DB_USERNAME` / `DB_PASSWORD` 配置，本地开发默认连接 localhost:3306
- **选课限制**：每个幼儿最多选 4 门兴趣课程
- **Service 注解**：所有 Service 类需 `@Service` 注解才能被 Spring 注入

## 表名前缀

所有表名以 `t_` 开头：`t_user`, `t_class_info`, `t_child`, `t_course`, `t_child_course`, `t_dish`, `t_weekly_menu`, `t_attendance`, `t_transfer_log`。

## 预置账号

admin/admin123（管理员），teacher01~teacher09/123456（教师，各绑定一个班级）。
