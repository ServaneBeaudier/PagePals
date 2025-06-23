FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /membership

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /membership

COPY --from=builder /membership/target/membership.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
