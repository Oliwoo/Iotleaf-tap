FROM openjdk:17-jdk-slim

# Imposta il directory di lavoro
WORKDIR /app

# Copia il JAR del server nel container
COPY server.jar app.jar

# Esegue il JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]