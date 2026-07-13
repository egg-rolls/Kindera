# 直接复制预构建的 jar
FROM amazoncorretto:8-alpine-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
