
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

COPY . .


RUN mvn -f back-end/pom.xml clean package -DskipTests


FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=build /app/back-end/target/*.jar app.jar

COPY wallet /app/wallet

ENV TNS_ADMIN=/app/wallet
EXPOSE 8080

ENTRYPOINT ["java", "-Xmx352m", "-jar", "app.jar"]
