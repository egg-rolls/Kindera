# 幼儿园管理系统 — 开发计划（PLAN.md）

> **项目名称：** Kindergarten Management System
> **创建日期：** 2026-07-06
> **最后更新：** 2026-07-13
> **开发语言：** Java 8+ / JavaScript (Vue 3)
> **数据库：** MySQL 8.x
> **架构：** 前后端分离（REST API）

---

## 项目结构

```
幼儿园管理系统/
├── PLAN.md                          // 本文件
├── README.md                        // 项目说明
├── DOCKER.md                        // Docker 部署指南
├── CONTRIBUTING.md                  // 贡献指南
├── CHANGELOG.md                     // 更新日志
├── docs/
│   ├── PRD-产品需求文档.md
│   ├── PBD-产品边界文档.md
│   ├── SPEC-技术规格文档.md
│   └── BUG-FIX-LOG.md
├── backend/                         // Spring Boot 后端
│   ├── src/main/java/kindergarten/
│   │   ├── controller/              // REST API 控制器（8个）
│   │   ├── service/                 // 业务逻辑层（7个）
│   │   ├── dao/                     // 数据访问层（9个）
│   │   ├── entity/                  // 实体类（9个）
│   │   ├── config/                  // 配置类
│   │   └── util/                    // 工具类
│   └── pom.xml
├── frontend/                        // Vue 3 前端
│   ├── src/
│   │   ├── views/                   // 页面组件（7个）
│   │   ├── api/                     // API 封装
│   │   └── router/                  // 路由配置
│   └── package.json
├── docker-compose.yml               // Docker 编排配置
├── src/                             // 旧版控制台程序（legacy）
└── tests/                           // 旧版测试代码
```

---

## 阶段一：基础设施层 ✅

### Step 1.1 — 工具类 + 数据库初始化
- [x] `src/kindergarten/util/DBUtil.java` — 数据库连接工具
- [x] `src/kindergarten/util/InputUtil.java` — 控制台输入工具
- [x] `src/kindergarten/util/InitDatabase.java` — 建库建表+预置数据

### Step 1.2 — 实体类（全部）
- [x] `src/kindergarten/entity/User.java`
- [x] `src/kindergarten/entity/ClassInfo.java`
- [x] `src/kindergarten/entity/Child.java`
- [x] `src/kindergarten/entity/Course.java`
- [x] `src/kindergarten/entity/ChildCourse.java`
- [x] `src/kindergarten/entity/Dish.java`
- [x] `src/kindergarten/entity/WeeklyMenu.java`
- [x] `src/kindergarten/entity/Attendance.java`
- [x] `src/kindergarten/entity/TransferLog.java`

---

## 阶段二：数据访问层（DAO） ✅

### Step 2.1 — 基础DAO
- [x] `src/kindergarten/dao/UserDao.java`
- [x] `src/kindergarten/dao/ClassDao.java`
- [x] `src/kindergarten/dao/ChildDao.java`
- [x] `src/kindergarten/dao/CourseDao.java`

### Step 2.2 — 扩展DAO
- [x] `src/kindergarten/dao/ChildCourseDao.java`
- [x] `src/kindergarten/dao/DishDao.java`
- [x] `src/kindergarten/dao/MenuDao.java`
- [x] `src/kindergarten/dao/AttendanceDao.java`
- [x] `src/kindergarten/dao/TransferLogDao.java`

---

## 阶段三：业务逻辑层（Service） ✅

### Step 3.1 — 核心业务
- [x] `src/kindergarten/service/UserService.java` — 登录认证
- [x] `src/kindergarten/service/ChildService.java` — 幼儿学籍CRUD
- [x] `src/kindergarten/service/CourseService.java` — 选课/退课/容量校验
- [x] `src/kindergarten/service/MenuService.java` — 菜品库+每周排餐

### Step 3.2 — 扩展业务
- [x] `src/kindergarten/service/AttendanceService.java` — 考勤打卡/统计
- [x] `src/kindergarten/service/TransferService.java` — 调班+日志
- [x] `src/kindergarten/service/StatisticsService.java` — 统计报表

---

## 阶段四：表现层（View） ✅

### Step 4.1 — 登录与主菜单
- [x] `src/kindergarten/view/LoginView.java` — 登录界面
- [x] `src/kindergarten/view/MainView.java` — 主菜单路由

### Step 4.2 — 管理员视图
- [x] `src/kindergarten/view/AdminView.java` — 管理员功能菜单
- [x] `src/kindergarten/view/ChildView.java` — 幼儿学籍操作界面
- [x] `src/kindergarten/view/CourseView.java` — 课程管理界面
- [x] `src/kindergarten/view/MenuView.java` — 食谱管理界面

### Step 4.3 — 教师视图 + 通用视图
- [x] `src/kindergarten/view/TeacherView.java` — 教师功能菜单
- [x] `src/kindergarten/view/AttendanceView.java` — 考勤操作界面
- [x] `src/kindergarten/view/StatisticsView.java` — 统计报表界面
- [x] `src/kindergarten/view/TransferView.java` — 调班操作界面

### Step 4.4 — 程序入口
- [x] `src/kindergarten/Main.java` — 主入口

---

## 阶段五：测试 ✅

- [x] `tests/kindergarten/InitDatabaseTest.java` — 数据库初始化验证
- [x] `tests/kindergarten/UserDaoTest.java` — 用户DAO测试
- [x] `tests/kindergarten/ChildDaoTest.java` — 幼儿DAO测试
- [x] `tests/kindergarten/ChildServiceTest.java` — 幼儿业务测试
- [x] `tests/kindergarten/CourseServiceTest.java` — 选课业务测试
- [x] `tests/kindergarten/AttendanceServiceTest.java` — 考勤测试
- [x] `tests/kindergarten/TransferServiceTest.java` — 调班测试
- [x] `tests/kindergarten/StatisticsServiceTest.java` — 统计测试
- [x] `tests/kindergarten/EdgeCaseTest.java` — 边界情况测试
- [x] `tests/kindergarten/SearchTest.java` — 搜索功能测试
- [x] `tests/kindergarten/SearchVerifyTest.java` — 搜索验证测试

---

## 阶段六：全栈 Web 应用 ✅

### Step 6.1 — Spring Boot 后端
- [x] `backend/src/main/java/kindergarten/controller/AuthController.java` — 认证控制器
- [x] `backend/src/main/java/kindergarten/controller/ChildController.java` — 幼儿控制器
- [x] `backend/src/main/java/kindergarten/controller/ClassController.java` — 班级控制器
- [x] `backend/src/main/java/kindergarten/controller/CourseController.java` — 课程控制器
- [x] `backend/src/main/java/kindergarten/controller/MenuController.java` — 食谱控制器
- [x] `backend/src/main/java/kindergarten/controller/AttendanceController.java` — 考勤控制器
- [x] `backend/src/main/java/kindergarten/controller/TransferController.java` — 调班控制器
- [x] `backend/src/main/java/kindergarten/controller/StatisticsController.java` — 统计控制器

### Step 6.2 — Vue 3 前端
- [x] `frontend/src/views/Login.vue` — 登录页面
- [x] `frontend/src/views/Layout.vue` — 布局组件
- [x] `frontend/src/views/Dashboard.vue` — 仪表盘
- [x] `frontend/src/views/Children.vue` — 幼儿管理
- [x] `frontend/src/views/Courses.vue` — 课程管理
- [x] `frontend/src/views/Menus.vue` — 食谱管理
- [x] `frontend/src/views/Attendance.vue` — 考勤管理
- [x] `frontend/src/views/Transfers.vue` — 调班管理
- [x] `frontend/src/api/index.js` — API 封装
- [x] `frontend/src/router/index.js` — 路由配置

### Step 6.3 — Docker 部署
- [x] `docker-compose.yml` — Docker 编排配置
- [x] `backend/Dockerfile` — 后端镜像构建
- [x] `frontend/Dockerfile` — 前端镜像构建
- [x] `frontend/nginx.conf` — Nginx 配置

---

## 阶段七：文档完善 ✅

- [x] `README.md` — 项目说明（更新为全栈应用）
- [x] `CLAUDE.md` — Claude Code 指南
- [x] `DOCKER.md` — Docker 部署指南
- [x] `CONTRIBUTING.md` — 贡献指南
- [x] `CHANGELOG.md` — 更新日志
- [x] `PLAN.md` — 开发计划（本文件）

---

## 完成标准

1. ✅ 所有文件完成并编译通过，零错误
2. ✅ 系统启动自动建库建表+预置数据
3. ✅ 管理员可完成全部8大模块操作
4. ✅ 教师可完成考勤、查看等操作
5. ✅ 所有测试类运行通过
6. ✅ 每个类有完整注释（作者、日期、功能、版本）
7. ✅ 前后端分离架构，REST API 完整
8. ✅ Docker Compose 一键部署
9. ✅ 项目文档完整

---

## 技术栈总结

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.18 |
| 后端语言 | Java | 8 |
| 数据库 | MySQL | 8.0 |
| 前端框架 | Vue | 3.3.4 |
| 构建工具 | Vite | 4.4.9 |
| UI 组件库 | Element Plus | 2.3.12 |
| HTTP 客户端 | Axios | 1.4.0 |
| 容器化 | Docker Compose | - |
| Web 服务器 | Nginx | Alpine |
