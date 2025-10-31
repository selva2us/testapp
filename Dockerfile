# Stage 1: Build the app
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the app (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose the Spring Boot port
EXPOSE 8080

# Set active profile to prod
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java","-jar","app.jar"]
