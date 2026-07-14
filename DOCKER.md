# Docker 部署指南

## 快速开始

### 前提条件
- Docker Desktop 已安装并启动

### 一键部署

```bash
# 克隆仓库
git clone https://github.com/egg-rolls/20260708.git
cd 20260708

# 启动所有服务
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
| teacher01 | 123456 | 教师（大一班） |
| teacher02 | 123456 | 教师（大二班） |
| teacher03 | 123456 | 教师（大三班） |
| teacher04 | 123456 | 教师（中一班） |
| teacher05 | 123456 | 教师（中二班） |
| teacher06 | 123456 | 教师（中三班） |
| teacher07 | 123456 | 教师（小一班） |
| teacher08 | 123456 | 教师（小二班） |
| teacher09 | 123456 | 教师（小三班） |

## 常用命令

```bash
# 查看运行状态
docker compose ps

# 查看日志
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql

# 停止服务
docker compose down

# 清理数据（删除数据库）
docker compose down -v

# 重建并启动
docker compose up -d --build

# 进入容器
docker exec -it kindergarten-backend sh
docker exec -it kindergarten-mysql mysql -uroot -plirui520
```

## Docker 配置说明

### 服务架构

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Frontend  │    │   Backend   │    │    MySQL    │
│   (Nginx)   │───▶│ (Spring Boot)│───▶│   8.0      │
│   Port 80   │    │  Port 8080  │    │  Port 3306  │
└─────────────┘    └─────────────┘    └─────────────┘
```

### 环境变量

后端服务使用以下环境变量配置数据库连接：

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `DB_HOST` | mysql | 数据库主机 |
| `DB_PORT` | 3306 | 数据库端口 |
| `DB_USERNAME` | root | 数据库用户名 |
| `DB_PASSWORD` | lirui520 | 数据库密码 |
| `SPRING_DATASOURCE_URL` | jdbc:mysql://mysql:3306/kindergarten... | JDBC 连接字符串 |

### 数据持久化

MySQL 数据存储在 Docker 数据卷 `mysql-data` 中，即使容器删除数据也不会丢失。

如需完全重置数据库：

```bash
docker compose down -v
docker compose up -d --build
```

## 使用的 Docker 镜像

| 服务 | 镜像 | 说明 |
|------|------|------|
| 后端 | amazoncorretto:8-alpine-jre | Amazon JDK 8 JRE |
| 前端构建 | node:18-alpine | Node.js 18 |
| 前端运行时 | nginx:alpine | Nginx |
| 数据库 | mysql:8.0 | MySQL 8.0 |

## 生产环境部署

### 使用 docker-compose.server.yml

生产环境建议使用 `docker-compose.server.yml`：

```bash
docker compose -f docker-compose.server.yml up -d --build
```

### 安全建议

1. 修改默认数据库密码
2. 配置 HTTPS（在 Nginx 中添加 SSL 证书）
3. 限制数据库端口对外暴露
4. 定期备份数据库

## 故障排查

### 查看服务状态

```bash
docker compose ps
```

### 查看日志

```bash
# 查看所有服务日志
docker compose logs

# 查看特定服务日志
docker compose logs backend
docker compose logs mysql
```

### 常见问题

1. **端口被占用**
   ```bash
   # 检查端口占用
   netstat -ano | findstr :80
   netstat -ano | findstr :8080
   netstat -ano | findstr :3306
   ```

2. **数据库连接失败**
   ```bash
   # 检查 MySQL 是否启动
   docker compose ps mysql
   
   # 查看 MySQL 日志
   docker compose logs mysql
   ```

3. **前端无法访问后端 API**
   - 检查 Nginx 配置是否正确代理 `/api` 到后端
   - 检查后端服务是否正常运行
