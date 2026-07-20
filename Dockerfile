# Estagio 1: O Render vai baixar o Maven e compilar o seu codigo
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estagio 2: O Render cria um ambiente Java limpo e super leve (Eclipse Temurin)
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# Agora copiamos cirurgicamente o arquivo exato, sem usar curingas
COPY --from=build /app/target/strideup.jar app.jar
# Inicia a aplicacao injetando a porta dinamica do Render
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]