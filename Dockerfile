# Etapa 1: Imagem base leve com OpenJDK 17 (Eclipse Temurin JRE)
FROM eclipse-temurin:17-jre-alpine

# Metadados do projeto
LABEL maintainer="ZooMed Fauna & Domesticos"
LABEL description="Sistema de Gestao Clinica e Consultoria Veterinaria"

# Define o diretorio de trabalho no container
WORKDIR /app

# Cria usuario nao-root para boas praticas de seguranca em producao
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia o artefato JAR gerado pelo Maven para o container
COPY target/*.jar app.jar

# Expoe a porta configurada no application.properties
EXPOSE 8080

# Variaveis de ambiente padrao (ativando profile dev/prod conforme necessario)
ENV SPRING_PROFILES_ACTIVE=dev
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Ponto de entrada para execucao da aplicacao Spring Boot
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
