FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /search

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /search

COPY --from=builder /search/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
