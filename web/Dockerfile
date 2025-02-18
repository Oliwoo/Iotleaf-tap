FROM openjdk:17-jdk-slim

# Imposta il directory di lavoro
WORKDIR /app

# Copia il JAR della tua applicazione nel container
COPY iotleaf/src/main/resources/static/images /app/static/images
COPY iotleaf/target/iotleaf-0.0.1-SNAPSHOT.jar app.jar

# Esegui il JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]