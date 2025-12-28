FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copia os arquivos do Maven Wrapper primeiro para aproveitar o cache das camadas
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dá permissão de execução ao wrapper e baixa as dependências
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copia o código fonte e gera o build
COPY src ./src
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

# Uso de shell form para garantir que o wildcard (*) funcione no nome do JAR
ENTRYPOINT ["sh", "-c", "java -jar target/farmacia-api-*.jar"]

ENV SPRING_PROFILES_ACTIVE=docker