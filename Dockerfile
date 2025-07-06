# ------------ Stage 1: Build -----------------
FROM gradle:8.14.2-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# ------------ Stage 2: Run -------------------
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
