# Stage 1: Extract layers from the JAR
FROM eclipse-temurin:21-jre-alpine as builder
WORKDIR application
COPY target/pneumonia-backend.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Stage 2: Final runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR application

# Copy layers separately to leverage Docker cache
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

EXPOSE 8080

# Use JarLauncher to start the application with layers
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]