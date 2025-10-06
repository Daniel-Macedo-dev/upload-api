FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/target/upload-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
ENV AWS_REGION=sa-east-1
ENV AWS_S3_BUCKET_NAME=prints-jogos

ENTRYPOINT ["java", "-jar", "app.jar"]
