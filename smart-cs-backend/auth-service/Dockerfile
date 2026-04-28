# 认证授权服务
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="smart-cs"
LABEL service="auth-service"

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms128m -Xmx256m"

RUN apk add --no-cache tzdata curl && \
    cp /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo "${TZ}" > /etc/timezone && \
    apk del tzdata

WORKDIR /app

COPY target/auth-service-*.jar app.jar

EXPOSE 8084

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8084/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.profiles.active=docker -jar app.jar"]
