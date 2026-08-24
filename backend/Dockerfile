# Stage 1: Build the application using Maven and JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml first to download dependencies
COPY pom.xml .

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the application source code
COPY src ./src

# Build the application (skipping tests for production build)
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image using JRE 17
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/globetrotter-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the default port
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
