FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /user

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /user

COPY --from=builder /user/target/user.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
