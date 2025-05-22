FROM maven:3.9.9-eclipse-temurin-21-alpine
EXPOSE 8080
EXPOSE 80
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]