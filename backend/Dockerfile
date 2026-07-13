# 直接使用预构建的 jar，不依赖 Maven 镜像
FROM eclipse-temurin:8-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
