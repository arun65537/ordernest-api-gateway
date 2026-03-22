FROM gradle:8.10.2-jdk17 AS build
WORKDIR /workspace

COPY build.gradle settings.gradle ./
COPY src src
RUN gradle --no-daemon clean bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/build/libs/ordernest-api-gateway-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8093
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
