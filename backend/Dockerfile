FROM maven:3.8.8-amazoncorretto-8 AS build
WORKDIR /app

# 构建
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# 运行
FROM amazoncorretto:8-alpine-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
