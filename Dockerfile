

FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar


COPY wallet /app/wallet


ENV TNS_ADMIN=/app/wallet

# Porta padrão que o Render espera
EXPOSE 8080

ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]