FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven files
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN echo "=== Making mvnw executable ===" && \
    chmod +x mvnw && \
    echo "=== Downloading dependencies ===" && \
    ./mvnw dependency:go-offline -B || true && \
    echo "=== Dependencies downloaded ==="

# Copy source - VERBOSE
COPY src ./src
RUN echo "=== Source copied successfully ===" && \
    ls -la src/ && \
    echo "=== Starting Maven build ===" && \
    ./mvnw clean package -DskipTests -B -X && \
    echo "=== Build complete ===" && \
    ls -la target/

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /tmp/uploads/event-images

EXPOSE 8080
#CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=railway -jar app.jar"]