FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY app/build/libs/app.jar app.jar

EXPOSE 9898

ENTRYPOINT ["java", "-jar", "app.jar"]
