FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY common ./common
COPY api-service ./api-service
COPY parsing-service ./parsing-service
COPY analysis-service ./analysis-service
RUN mvn -q -pl parsing-service -am package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /build/parsing-service/target/parsing-service.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
