FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=build/libs/*.jar

RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

ENV TZ=Asia/Seoul

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --chown=spring:spring ${JAR_FILE} app.jar

HEALTHCHECK --start-period=30s --interval=10s --timeout=5s --retries=5 \
  CMD sh -c 'curl -f http://localhost:8080/api/actuator/health || exit 1'

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
