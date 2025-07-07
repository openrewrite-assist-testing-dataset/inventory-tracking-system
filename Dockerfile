FROM openjdk:14-jre-slim

WORKDIR /app

COPY build/libs/inventory-tracking-system-1.0.0-all.jar app.jar
COPY src/main/resources/config.yml config.yml

EXPOSE 8080 8081

ENTRYPOINT ["java", "-jar", "app.jar", "server", "config.yml"]