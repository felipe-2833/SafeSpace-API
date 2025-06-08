# Etapa 1: Build da aplicação
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime da aplicação
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/safespace-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]