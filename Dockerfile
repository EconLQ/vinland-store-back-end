FROM maven:3.8.6-openjdk-18 AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:17

COPY --from=build /app/target/*.jar /app.jar

WORKDIR /app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
