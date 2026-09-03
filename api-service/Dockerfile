# Multi-stage build: compiles the whole reactor (parent + common + this module) since Maven
# needs the full module tree to resolve the jobfit-common dependency, then ships only the
# built jar in a slim JRE image.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY common ./common
COPY api-service ./api-service
COPY parsing-service ./parsing-service
COPY analysis-service ./analysis-service
RUN mvn -q -pl api-service -am package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /build/api-service/target/api-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
