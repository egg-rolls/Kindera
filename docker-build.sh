#!/bin/bash
# 一键构建部署脚本
set -e

echo "══════ 幼儿园管理系统 Docker 部署 ══════"

# 1. 本地构建后端 jar
echo "[1/3] 构建后端..."
cd backend
mvn clean package -DskipTests -q
cd ..
echo "  ✓ 后端构建完成"

# 2. 安装前端依赖
echo "[2/3] 构建前端..."
cd frontend
npm install -q 2>/dev/null
cd ..
echo "  ✓ 前端构建完成"

# 3. Docker Compose 启动
echo "[3/3] 启动 Docker 服务..."
docker compose up -d --build
echo "  ✓ 服务启动完成"

echo ""
echo "══════ 部署成功 ══════"
echo "  前端: http://localhost"
echo "  后端: http://localhost:8080"
echo "  数据库: localhost:3306"
echo ""
echo "  预置账号: admin / admin123"
