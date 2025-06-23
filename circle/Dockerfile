# Étape 1 : build
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /circle

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Étape 2 : exécution
FROM openjdk:21-slim

WORKDIR /circle

COPY --from=builder /circle/target/circle.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
