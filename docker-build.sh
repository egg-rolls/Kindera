#!/bin/bash
# 幼儿园管理系统 Docker 部署脚本
set -e

echo "══════ 幼儿园管理系统 Docker 部署 ══════"

# 1. 使用 Docker 运行 Maven 构建后端
echo "[1/3] 使用 Docker 构建后端..."
docker run --rm -v "$(pwd)/backend:/app" -w /app amazoncorretto:8 sh -c "curl -sL https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz | tar xz -C /opt && /opt/apache-maven-3.8.8/bin/mvn clean package -DskipTests -q"
echo "  ✓ 后端构建完成"

# 2. 启动 Docker 服务
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
