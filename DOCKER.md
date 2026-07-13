# Docker 部署指南

## 快速开始

### 前提条件
- Docker Desktop 已安装并启动

### 一键部署

**Windows (PowerShell):**
```powershell
.\build.ps1
```

**Windows (CMD):**
```cmd
build.bat
```

**Linux/Mac:**
```bash
bash docker-build.sh
```

### 手动部署

```bash
# 1. 使用 Docker 运行 Maven 构建后端
docker run --rm -v "$(pwd)/backend:/app" -w /app amazoncorretto:8 sh -c "curl -sL https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz | tar xz -C /opt && /opt/apache-maven-3.8.8/bin/mvn clean package -DskipTests"

# 2. 启动所有服务
docker compose up -d --build
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
# 查看运行状态
docker compose ps

# 查看日志
docker compose logs -f backend

# 停止服务
docker compose down

# 清理数据（删除数据库）
docker compose down -v

# 重建并启动
docker compose up -d --build
```

## 使用的 Docker 镜像

| 服务 | 镜像 | 说明 |
|------|------|------|
| 后端运行时 | amazoncorretto:8-alpine-jre | Amazon JDK 8 JRE |
| 前端构建 | node:18-alpine | Node.js 18 |
| 前端运行时 | nginx:alpine | Nginx |
| 数据库 | mysql:8.0 | MySQL 8.0 |
