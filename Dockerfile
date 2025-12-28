FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/farmacia-api-0.0.1-SNAPSHOT.jar"]

ENV SPRING_PROFILES_ACTIVE=docker