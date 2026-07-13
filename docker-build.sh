#!/bin/bash
# 一键构建部署脚本（使用 Docker 运行 Maven）
set -e

echo "══════ 幼儿园管理系统 Docker 部署 ══════"

# 1. 用 Docker 运行 Maven 构建后端
echo "[1/3] 构建后端（Docker Maven）..."
docker run --rm \
  -v "$(pwd)/backend":/usr/src/app \
  -w /usr/src/app \
  maven:3.9.6-eclipse-temurin-17 \
  mvn clean package -DskipTests -q
echo "  ✓ 后端构建完成"

# 2. Docker Compose 启动所有服务
echo "[2/3] 启动 Docker 服务..."
docker compose up -d --build
echo "  ✓ 服务启动完成"

echo ""
echo "══════ 部署成功 ══════"
echo "  前端: http://localhost"
echo "  后端: http://localhost:8080"
echo "  数据库: localhost:3306"
echo ""
echo "  预置账号: admin / admin123"
