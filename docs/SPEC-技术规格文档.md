# 幼儿园管理系统 — 技术规格文档（SPEC）

> **文档版本：** v2.0
> **创建日期：** 2026-07-06
> **最后更新：** 2026-07-13
> **文档性质：** 课程大作业 — 技术设计规格说明

---

## 1. 系统架构设计

### 1.1 整体架构图

```
┌──────────────────────────────────────────────────────────────┐
│                      前端层 (Frontend)                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Vue 3 + Vite + Element Plus                         │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐│   │
│  │  │  Login   │ │Children  │ │ Courses  │ │  Menus   ││   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘│   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐             │   │
│  │  │Attendance│ │Transfers │ │Dashboard │             │   │
│  │  └──────────┘ └──────────┘ └──────────┘             │   │
│  └──────────────────────────────────────────────────────┘   │
├──────────────────────────────────────────────────────────────┤
│                      Nginx 反向代理                           │
│                    (端口 80, /api 代理)                        │
├──────────────────────────────────────────────────────────────┤
│                      后端层 (Backend)                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Spring Boot 2.7.18 REST API                         │   │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐       │   │
│  │  │Controller  │ │  Service   │ │    DAO     │       │   │
│  │  │  (8个)     │ │   (7个)    │ │   (9个)    │       │   │
│  │  └────────────┘ └────────────┘ └────────────┘       │   │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐       │   │
│  │  │  Entity    │ │  Config    │ │   Util     │       │   │
│  │  │  (9个)     │ │   (1个)    │ │   (3个)    │       │   │
│  │  └────────────┘ └────────────┘ └────────────┘       │   │
│  └──────────────────────────────────────────────────────┘   │
├──────────────────────────────────────────────────────────────┤
│                      数据库层 (Database)                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  MySQL 8.0                                           │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐│   │
│  │  │t_user    │ │t_class   │ │t_child   │ │t_course  ││   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘│   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐│   │
│  │  │t_child   │ │t_dish    │ │t_weekly  │ │t_attend  ││   │
│  │  │_course   │ │          │ │_menu     │ │ance      ││   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘│   │
│  │  ┌──────────┐                                        │   │
│  │  │t_transfer│                                        │   │
│  │  │_log      │                                        │   │
│  │  └──────────┘                                        │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

### 1.2 前后端分离架构

```
┌─────────────┐    HTTP/REST    ┌─────────────┐    JDBC    ┌─────────────┐
│   Frontend  │ ◄─────────────► │   Backend   │ ◄────────► │   MySQL     │
│   (Vue 3)   │    JSON API     │ (Spring Boot)│            │   8.0       │
│   Port 80   │                 │  Port 8080  │            │  Port 3306  │
└─────────────┘                 └─────────────┘            └─────────────┘
```

### 1.3 后端包结构设计

```
kindergarten
├── controller      // REST API 控制器
│   ├── AuthController.java
│   ├── ChildController.java
│   ├── ClassController.java
│   ├── CourseController.java
│   ├── MenuController.java
│   ├── AttendanceController.java
│   ├── TransferController.java
│   └── StatisticsController.java
│
├── service         // 业务逻辑层
│   ├── UserService.java
│   ├── ChildService.java
│   ├── ClassService.java
│   ├── CourseService.java
│   ├── MenuService.java
│   ├── AttendanceService.java
│   ├── TransferService.java
│   └── StatisticsService.java
│
├── dao             // 数据访问层
│   ├── UserDao.java
│   ├── ChildDao.java
│   ├── ClassDao.java
│   ├── CourseDao.java
│   ├── ChildCourseDao.java
│   ├── DishDao.java
│   ├── MenuDao.java
│   ├── AttendanceDao.java
│   └── TransferLogDao.java
│
├── entity          // 实体类
│   ├── User.java
│   ├── ClassInfo.java
│   ├── Child.java
│   ├── Course.java
│   ├── ChildCourse.java
│   ├── Dish.java
│   ├── WeeklyMenu.java
│   ├── Attendance.java
│   └── TransferLog.java
│
├── config          // 配置类
│   └── CorsConfig.java
│
├── exception       // 异常类
│   └── DataAccessException.java
│
└── util            // 工具类
    ├── DBUtil.java
    ├── PasswordUtil.java
    └── InitDatabase.java
```

### 1.4 前端结构设计

```
frontend
├── src/
│   ├── views/              // 页面组件
│   │   ├── Login.vue       // 登录页面
│   │   ├── Layout.vue      // 布局组件
│   │   ├── Dashboard.vue   // 仪表盘
│   │   ├── Children.vue    // 幼儿管理
│   │   ├── Courses.vue     // 课程管理
│   │   ├── Menus.vue       // 食谱管理
│   │   ├── Attendance.vue  // 考勤管理
│   │   └── Transfers.vue   // 调班管理
│   │
│   ├── api/                // API 封装
│   │   └── index.js        // Axios 实例和请求方法
│   │
│   ├── router/             // 路由配置
│   │   └── index.js        // Vue Router 配置
│   │
│   ├── App.vue             // 根组件
│   └── main.js             // 入口文件
│
├── index.html              // HTML 模板
├── package.json            // npm 配置
└── vite.config.js          // Vite 配置
```

## 2. 数据库设计

### 2.1 ER关系概览

```
User ──────── 1:N ──── (独立表，无外键关联)

ClassInfo ─── 1:N ──── Child ──── N:M ──── Course
                          │                  │
                          │                  │
                          ▼                  ▼
                     Attendance        ChildCourse

Dish ──────── 1:N ──── WeeklyMenu

Child ─────── 1:N ──── TransferLog
ClassInfo ── 1:N ──── TransferLog
```

### 2.2 表结构定义

#### 表1：用户表 (t_user)

```sql
CREATE TABLE t_user (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(30) NOT NULL UNIQUE    COMMENT '用户名',
    password    VARCHAR(100) NOT NULL          COMMENT '密码（SHA-256哈希）',
    real_name   VARCHAR(30) NOT NULL           COMMENT '真实姓名',
    role        TINYINT NOT NULL DEFAULT 2     COMMENT '角色：1=管理员 2=教师',
    class_id    INT DEFAULT NULL               COMMENT '教师负责班级ID（管理员为NULL）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (class_id) REFERENCES t_class_info(id)
) COMMENT '用户表';
```

#### 表2：班级表 (t_class_info)

```sql
CREATE TABLE t_class_info (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name  VARCHAR(20) NOT NULL UNIQUE    COMMENT '班级名称（如：大一班）',
    grade       VARCHAR(10) NOT NULL           COMMENT '年级（大班/中班/小班）',
    max_count   INT NOT NULL DEFAULT 10        COMMENT '班级最大人数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '班级信息表';
```

#### 表3：幼儿表 (t_child)

```sql
CREATE TABLE t_child (
    id              INT PRIMARY KEY AUTO_INCREMENT COMMENT '幼儿ID',
    name            VARCHAR(50) NOT NULL           COMMENT '姓名',
    gender          CHAR(1) NOT NULL               COMMENT '性别：M=男 F=女',
    birth_date      DATE NOT NULL                  COMMENT '出生日期',
    parent_name     VARCHAR(50) NOT NULL           COMMENT '家长姓名',
    parent_phone    VARCHAR(20) NOT NULL           COMMENT '家长电话',
    class_id        INT NOT NULL                   COMMENT '所在班级ID',
    enrollment_date DATE NOT NULL                  COMMENT '入园日期',
    status          TINYINT NOT NULL DEFAULT 1     COMMENT '状态：1=在园 0=离园',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (class_id) REFERENCES t_class_info(id)
) COMMENT '幼儿信息表';
```

#### 表4：课程表 (t_course)

```sql
CREATE TABLE t_course (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_name VARCHAR(50) NOT NULL UNIQUE    COMMENT '课程名称',
    max_count   INT NOT NULL DEFAULT 15        COMMENT '课程最大人数',
    description VARCHAR(200) DEFAULT NULL      COMMENT '课程描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '兴趣课程表';
```

#### 表5：幼儿选课表 (t_child_course)

```sql
CREATE TABLE t_child_course (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    child_id    INT NOT NULL                   COMMENT '幼儿ID',
    course_id   INT NOT NULL                   COMMENT '课程ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    UNIQUE KEY uk_child_course (child_id, course_id) COMMENT '同一幼儿不可重复选同一课程',
    FOREIGN KEY (child_id) REFERENCES t_child(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES t_course(id)
) COMMENT '幼儿选课关系表';
```

#### 表6：菜品表 (t_dish)

```sql
CREATE TABLE t_dish (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID',
    dish_name   VARCHAR(50) NOT NULL           COMMENT '菜品名称',
    dish_type   TINYINT NOT NULL               COMMENT '类型：1=主食 2=荤菜 3=素菜 4=汤 5=水果',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '菜品库表';
```

#### 表7：每周食谱表 (t_weekly_menu)

```sql
CREATE TABLE t_weekly_menu (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    week_start  DATE NOT NULL                  COMMENT '周起始日期（周一）',
    day_of_week TINYINT NOT NULL               COMMENT '星期几：1=周一 ... 5=周五',
    meal_type   TINYINT NOT NULL               COMMENT '餐次：1=早餐 2=午餐 3=晚餐',
    dish_id     INT NOT NULL                   COMMENT '菜品ID',
    FOREIGN KEY (dish_id) REFERENCES t_dish(id)
) COMMENT '每周食谱表';
```

#### 表8：考勤表 (t_attendance)

```sql
CREATE TABLE t_attendance (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    child_id    INT NOT NULL                   COMMENT '幼儿ID',
    attend_date DATE NOT NULL                  COMMENT '考勤日期',
    status      TINYINT NOT NULL               COMMENT '状态：1=出勤 2=缺勤 3=请假 4=迟到',
    remark      VARCHAR(100) DEFAULT NULL      COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    UNIQUE KEY uk_child_date (child_id, attend_date) COMMENT '同一幼儿每天一条记录',
    FOREIGN KEY (child_id) REFERENCES t_child(id) ON DELETE CASCADE
) COMMENT '考勤记录表';
```

#### 表9：调班记录表 (t_transfer_log)

```sql
CREATE TABLE t_transfer_log (
    id              INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    child_id        INT NOT NULL                   COMMENT '幼儿ID',
    old_class_id    INT NOT NULL                   COMMENT '原班级ID',
    new_class_id    INT NOT NULL                   COMMENT '新班级ID',
    operator_id     INT NOT NULL                   COMMENT '操作人ID',
    transfer_date   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '调班时间',
    remark          VARCHAR(100) DEFAULT NULL      COMMENT '备注',
    FOREIGN KEY (child_id) REFERENCES t_child(id),
    FOREIGN KEY (old_class_id) REFERENCES t_class_info(id),
    FOREIGN KEY (new_class_id) REFERENCES t_class_info(id),
    FOREIGN KEY (operator_id) REFERENCES t_user(id)
) COMMENT '调班记录表';
```

## 3. API 接口设计

### 3.1 REST API 端点

| 路径 | 方法 | 说明 | 请求参数 |
|------|------|------|----------|
| `/api/auth/login` | POST | 用户登录 | `{username, password}` |
| `/api/children` | GET | 获取幼儿列表 | `?classId=&name=` |
| `/api/children` | POST | 添加幼儿 | `{name, gender, birthDate, ...}` |
| `/api/children/{id}` | PUT | 修改幼儿 | `{name, gender, ...}` |
| `/api/children/{id}` | DELETE | 删除幼儿 | - |
| `/api/classes` | GET | 获取班级列表 | - |
| `/api/courses` | GET | 获取课程列表 | - |
| `/api/courses` | POST | 添加课程 | `{courseName, maxCount}` |
| `/api/courses/{id}` | PUT | 修改课程 | `{courseName, maxCount}` |
| `/api/courses/{id}` | DELETE | 删除课程 | - |
| `/api/menus` | GET | 获取食谱 | `?weekStart=` |
| `/api/menus` | POST | 添加食谱 | `{weekStart, dayOfWeek, ...}` |
| `/api/attendance` | GET | 获取考勤记录 | `?childId=&date=` |
| `/api/attendance` | POST | 添加考勤 | `{childId, date, status}` |
| `/api/attendance/batch` | POST | 批量考勤 | `{date, records: [...]}` |
| `/api/transfers` | POST | 调班操作 | `{childId, newClassId}` |
| `/api/statistics/*` | GET | 数据统计 | - |

### 3.2 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

## 4. 核心类设计

### 4.1 Controller 示例 — ChildController.java

```java
@RestController
@RequestMapping("/api/children")
public class ChildController {
    
    @Autowired
    private ChildService childService;
    
    @GetMapping
    public List<Child> getChildren(
            @RequestParam(required = false) Integer classId,
            @RequestParam(required = false) String name) {
        return childService.getChildren(classId, name);
    }
    
    @PostMapping
    public Child addChild(@RequestBody Child child) {
        return childService.addChild(child);
    }
    
    @PutMapping("/{id}")
    public Child updateChild(@PathVariable int id, @RequestBody Child child) {
        child.setId(id);
        return childService.updateChild(child);
    }
    
    @DeleteMapping("/{id}")
    public void deleteChild(@PathVariable int id) {
        childService.deleteChild(id);
    }
}
```

### 4.2 Service 示例 — CourseService.java

```java
@Service
public class CourseService {
    
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private ChildCourseDao childCourseDao;
    
    /**
     * 为幼儿选课
     * 业务规则：
     *   1. 每人最多选4门课
     *   2. 不可重复选同一门课
     *   3. 课程人数未达上限
     */
    public String selectCourse(int childId, int courseId) {
        // 1. 查询该幼儿已选课程数
        int count = childCourseDao.countByChildId(childId);
        if (count >= 4) {
            return "选课失败：该幼儿已选满4门课程";
        }
        // 2. 检查是否重复选课
        if (childCourseDao.exists(childId, courseId)) {
            return "选课失败：该幼儿已选过此课程";
        }
        // 3. 检查课程容量
        int currentCount = childCourseDao.countByCourseId(courseId);
        int maxCount = courseDao.selectById(courseId).getMaxCount();
        if (currentCount >= maxCount) {
            return "选课失败：该课程已达人数上限（" + maxCount + "人）";
        }
        // 4. 执行选课
        childCourseDao.insert(childId, courseId);
        return "选课成功";
    }
}
```

### 4.3 前端 API 封装 — api/index.js

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export default api
```

## 5. Docker 部署架构

### 5.1 服务编排

```yaml
# docker-compose.yml
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
    
  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
```

### 5.2 网络架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Network                            │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │   Frontend  │    │   Backend   │    │    MySQL    │     │
│  │   (Nginx)   │───▶│ (Spring Boot)│───▶│   8.0      │     │
│  │   Port 80   │    │  Port 8080  │    │  Port 3306  │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

## 6. 预置数据初始化

系统首次运行时，`InitDatabase` 类自动执行：

```java
/**
 * 数据库初始化类
 *
 * @author [编写者姓名]
 * @date 2026-07-xx
 * @version 1.0
 * @description 首次运行时自动创建数据库、建表、插入预置数据
 */
public class InitDatabase {

    public void init() {
        // 1. 创建数据库 kindergarten（如不存在）
        // 2. 创建所有9张表（如不存在）
        // 3. 插入预置数据：
        //    - 9个班级（大一~大三、中一~中三、小一~小三）
        //    - 4门兴趣课程（舞蹈、跆拳道、钢琴、美术，容量各15人）
        //    - 1个管理员账号（admin / admin123）
        //    - 9个教师账号（teacher01~teacher09，对应9个班级）
        //    - 90个幼儿样本数据（每班10人）
        //    - 20个基础菜品
    }
}
```

## 7. 技术栈总结

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 后端框架 | Spring Boot | 2.7.18 | REST API 服务 |
| 后端语言 | Java | 8 | 兼容性最好 |
| 数据库 | MySQL | 8.0 | 关系型数据库 |
| 数据库驱动 | MySQL Connector/J | 8.0.33 | JDBC 驱动 |
| 前端框架 | Vue | 3.3.4 | 响应式 UI |
| 构建工具 | Vite | 4.4.9 | 快速构建 |
| UI 组件库 | Element Plus | 2.3.12 | 企业级组件 |
| HTTP 客户端 | Axios | 1.4.0 | API 请求 |
| 容器化 | Docker Compose | - | 一键部署 |
| Web 服务器 | Nginx | Alpine | 静态托管 + 反向代理 |

## 8. 开发环境要求

### 本地开发
- JDK 8+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

### Docker 部署
- Docker Desktop
- 至少 4GB 可用内存
