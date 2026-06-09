# syntax=docker/dockerfile:1

# --- Build React dashboard ---
FROM node:22-alpine AS web-build
WORKDIR /app/web
COPY web/package.json web/package-lock.json ./
RUN npm ci
COPY web/ ./
RUN npm run build

# --- Build Spring Boot backend (embeds web/dist as static assets) ---
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml ./
RUN mvn -B dependency:go-offline
COPY backend/src ./src
COPY --from=web-build /app/web/dist ./src/main/resources/static
RUN mvn -B package -DskipTests

# --- Runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache wget \
    && addgroup -S resourcelyi && adduser -S resourcelyi -G resourcelyi

COPY --from=backend-build /app/backend/target/resourcelyi-backend-3.3.0.jar app.jar

USER resourcelyi

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/api/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
