# 一键构建部署脚本（Windows PowerShell）
$ErrorActionPreference = "Stop"

Write-Host "══════ 幼儿园管理系统 Docker 部署 ══════" -ForegroundColor Cyan

# 1. 用 Docker 运行 Maven 构建后端
Write-Host "[1/3] 构建后端（Docker Maven）..." -ForegroundColor Yellow
docker run --rm -v "${PWD}\backend":/usr/src/app -w /usr/src/app maven:3.9.6-eclipse-temurin-17 mvn clean package -DskipTests -q
Write-Host "  ✓ 后端构建完成" -ForegroundColor Green

# 2. Docker Compose 启动所有服务
Write-Host "[2/3] 启动 Docker 服务..." -ForegroundColor Yellow
docker compose up -d --build
Write-Host "  ✓ 服务启动完成" -ForegroundColor Green

Write-Host ""
Write-Host "══════ 部署成功 ══════" -ForegroundColor Cyan
Write-Host "  前端: http://localhost"
Write-Host "  后端: http://localhost:8080"
Write-Host "  数据库: localhost:3306"
Write-Host ""
Write-Host "  预置账号: admin / admin123"
