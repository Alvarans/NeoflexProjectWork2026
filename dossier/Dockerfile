FROM eclipse-temurin:21-jre

WORKDIR /app

COPY dossier/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]