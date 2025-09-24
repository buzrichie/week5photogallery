# --- Stage 1: Build the application ---
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy project files into container
COPY . .

# Make mvnw executable (fixes permission denied error)
RUN chmod +x mvnw

# Build the application (skip tests for speed)
RUN ./mvnw package -DskipTests

# --- Stage 2: Run the application ---
FROM eclipse-temurin:21-jdk-alpine AS runner

# Set working directory
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Set default command to run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]