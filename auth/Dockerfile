# Étape 1 : build avec Maven (avec Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /auth

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Étape 2 : image d'exécution légère avec Java seulement
FROM openjdk:21-slim

WORKDIR /auth

COPY --from=builder /auth/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
