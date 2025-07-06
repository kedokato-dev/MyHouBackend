# ------------ Stage 1: Build -----------------
FROM gradle:8.14.2-jdk17 AS builder
WORKDIR /app

# Tách riêng dependencies để cache tốt hơn
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon || true

# Chỉ copy source khi cần build, giảm cache miss
COPY . .

# Build jar tối ưu cho production, loại bỏ test để build nhanh hơn trên Render
RUN gradle bootJar --no-daemon -x test

# ------------ Stage 2: Run -------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JAR từ build stage (tìm file jar thực tế)
COPY --from=builder /app/build/libs/*.jar app.jar

# Tối ưu RAM dùng cho JVM (cấu hình khuyến nghị cho Render free: 512MB RAM)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -Xmx340m -XX:+UseSerialGC -Dspring.profiles.active=prod"

EXPOSE 8080

# Giảm thời gian khởi động JVM và RAM sử dụng
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]