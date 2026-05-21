FROM eclipse-temurin:21-jre

# Создаём непривилегированного пользователя
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --gid 1001 appuser

WORKDIR /app
COPY dossier/target/*.jar app.jar
RUN chown -R appuser:appgroup /app
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]