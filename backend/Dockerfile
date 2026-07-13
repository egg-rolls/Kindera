FROM amazoncorretto:8 AS build
WORKDIR /app

# 安装 Maven
RUN yum install -y tar gzip && \
    curl -sL https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz | tar xz -C /opt
ENV M2_HOME=/opt/apache-maven-3.8.8
ENV PATH=$M2_HOME/bin:$PATH

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
