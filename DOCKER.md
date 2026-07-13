# Docker 部署指南

## 快速开始

### 前提条件
- Docker 20.0+
- Docker Compose 2.0+

### 一键部署

```bash
# 克隆项目
git clone https://github.com/egg-rolls/20260708.git
cd 20260708

# 启动所有服务
docker-compose up -d

# 查看运行状态
docker-compose ps
```

### 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端页面 | http://localhost | Vue 3 管理后台 |
| 后端 API | http://localhost:8080 | Spring Boot REST API |
| 数据库 | localhost:3306 | MySQL 8.0 |

### 预置账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| teacher01 | 123456 | 教师 |

## 常用命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 查看日志
docker-compose logs -f backend

# 重建并启动
docker-compose up -d --build

# 清理数据（删除数据库）
docker-compose down -v
```

## 开发模式

### 后端开发
```bash
cd backend
mvn spring-boot:run
```

### 前端开发
```bash
cd frontend
npm install
npm run dev
```

## 架构说明

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Vue 3 前端     │────▶│  Spring Boot    │────▶│   MySQL 数据库   │
│  (Nginx容器)     │     │   (Java容器)     │     │  (MySQL容器)     │
│   Port: 80      │     │   Port: 8080    │     │   Port: 3306    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```
