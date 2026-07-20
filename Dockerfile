# Estagio 1: O Render vai baixar o Maven e compilar o seu codigo
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Compila o projeto e logo em seguida apaga o plain.jar para evitar conflito
RUN mvn clean package -DskipTests && rm -f target/*-plain.jar

# Estagio 2: O Render cria um ambiente Java limpo e super leve (Eclipse Temurin)
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# Agora o asterisco so vai encontrar o JAR correto (o Fat JAR)
COPY --from=build /app/target/*.jar app.jar
# Permite que o Spring Boot use a porta que o Render definir na nuvem
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]