# Build 
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o POM primeiro, dps a src
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests


#runtime 
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# roda como non-root user, para maior seguranca
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar" "app.jar"]
