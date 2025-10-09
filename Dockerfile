# Étape 1 : build du projet avec Gradle
FROM gradle:8.3-jdk17 AS build
WORKDIR /home/ktor
COPY . .
# RUN gradle installDist
RUN chmod +x ./gradlew
RUN ./gradlew installDist --no-daemon

# Étape 2 : image finale pour exécution
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/ktor/build/install /app/install
EXPOSE 8080
ENV PORT=8080
CMD ["/app/install/MyApiKtor/bin/MyApiKtor"]
