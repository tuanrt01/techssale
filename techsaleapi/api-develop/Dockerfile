# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY ./ ./
RUN mvn clean install -DskipTests

# Run stage
FROM openjdk:17-jdk-bullseye
WORKDIR /app
COPY --from=build /app/target/techsale-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 80

CMD ["java","-jar","app.jar"]