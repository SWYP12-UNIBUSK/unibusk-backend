FROM eclipse-temurin:17-jre
ENV TZ=Asia/Seoul
ARG JAR_FILE=build/libs/*.jar

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN addgroup --system spring && adduser --system --group spring
USER spring:spring

COPY --chown=spring:spring ${JAR_FILE} app.jar

HEALTHCHECK --start-period=30s --interval=10s --timeout=3s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]