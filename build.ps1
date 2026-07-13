# 幼儿园管理系统 Docker 部署脚本
Write-Host "══════ 幼儿园管理系统 Docker 部署 ══════" -ForegroundColor Cyan

# 1. 使用 Docker 运行 Maven 构建后端
Write-Host "[1/3] 使用 Docker 构建后端..." -ForegroundColor Yellow
docker run --rm -v "${PWD}\backend:/app" -w /app amazoncorretto:8 sh -c "curl -sL https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz | tar xz -C /opt && /opt/apache-maven-3.8.8/bin/mvn clean package -DskipTests -q"
if ($LASTEXITCODE -ne 0) {
    Write-Host "  构建失败，请检查网络连接" -ForegroundColor Red
    exit 1
}
Write-Host "  ✓ 后端构建完成" -ForegroundColor Green

# 2. 启动 Docker 服务
Write-Host "[2/3] 启动 Docker 服务..." -ForegroundColor Yellow
docker compose up -d --build
Write-Host "  ✓ 服务启动完成" -ForegroundColor Green

Write-Host ""
Write-Host "══════ 部署成功 ══════" -ForegroundColor Cyan
Write-Host "  前端: http://localhost" -ForegroundColor White
Write-Host "  后端: http://localhost:8080" -ForegroundColor White
Write-Host "  数据库: localhost:3306" -ForegroundColor White
Write-Host ""
Write-Host "  预置账号: admin / admin123" -ForegroundColor White
