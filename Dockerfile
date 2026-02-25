## Multi-stage build for Spring Boot app
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven descriptor and download dependencies first (build cache)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy source code and build jar
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built jar from builder image
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Allow overriding config by environment variables (datasource, etc.)
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

