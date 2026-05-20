FROM eclipse-temurin:21-jre

WORKDIR /app

COPY deal/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]