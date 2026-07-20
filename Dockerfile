# Estagio 1: O Render vai baixar o Maven e compilar o seu código
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estagio 2: O Render cria um ambiente Java limpo e super leve apenas para rodar a aplicação
FROM openjdk:17-jdk-slim
WORKDIR /app
# Pega o arquivo compilado no estagio 1
COPY --from=build /app/target/*.jar app.jar
# Permite que o Spring Boot use a porta que o Render definir na nuvem
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]