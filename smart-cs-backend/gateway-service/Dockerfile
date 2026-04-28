# API网关服务
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="smart-cs"
LABEL service="gateway-service"

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms256m -Xmx512m"

RUN apk add --no-cache tzdata curl && \
    cp /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo "${TZ}" > /etc/timezone && \
    apk del tzdata

WORKDIR /app

COPY target/gateway-service-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.profiles.active=docker -jar app.jar"]
