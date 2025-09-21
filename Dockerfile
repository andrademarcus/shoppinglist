FROM amazoncorretto:17.0.16-al2023

WORKDIR /app

# cache deps
COPY gradlew settings.gradle build.gradle gradle/ ./
RUN chmod +x gradlew && ./gradlew --version

# build
COPY src ./src
RUN ./gradlew clean bootJar --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# copy fat jar
COPY --from=build /app/build/libs/*-*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]