FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jdk-alpine as runtime

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

COPY --from=builder --chown=spring:spring /app/target/*.jar /app/app.jar

ENV PORT=8080

EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar /app/app.jar"]