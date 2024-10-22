# Stage 1: Build
FROM maven:3.9.3-eclipse-temurin-17-alpine@sha256:1cbc71cb8e2f594338f4b4cbca897b9f9ed6183e361489f1f7db770d57efe839 AS builder

WORKDIR /build
COPY api-definition/api.yaml ./api-definition/api.yaml
COPY api-definition/pom.xml ./api-definition/pom.xml
COPY api-generated/pom.xml ./api-generated/pom.xml
COPY service/pom.xml ./service/pom.xml
COPY service/src ./service/src
COPY pom.xml ./pom.xml

RUN mvn clean install

# Stage 2: Runtime
FROM eclipse-temurin:17.0.6_10-jre-alpine@sha256:c26a727c4883eb73d32351be8bacb3e70f390c2c94f078dc493495ed93c60c2f

WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /build/service/target/*.jar /app/MultiTripFinder.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "MultiTripFinder.jar"]