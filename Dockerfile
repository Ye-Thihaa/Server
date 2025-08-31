FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper & pom.xml first (for caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# ✅ Give execute permission to mvnw
RUN chmod +x mvnw

# Download dependencies (caches dependencies in Docker layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# -------------------------
# Final Image
# -------------------------
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
