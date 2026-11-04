# ---------- ETAPA 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- ETAPA 2: imagen final ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8802

ENTRYPOINT ["java", "-jar", "app.jar"]