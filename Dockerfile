# Use official Eclipse Temurin JDK 21 as base image
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper & pom.xml to leverage caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (helps with build caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# -------------------------
# Final Image (Slim Runtime)
# -------------------------
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
