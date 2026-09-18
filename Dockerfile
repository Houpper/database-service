FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Dependências necessárias
RUN apk add --no-cache wget tzdata

# Usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring

# Timezone
ENV TZ=America/Sao_Paulo

# Copia aplicação
COPY build/libs/database-service.jar ./app.jar

EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
CMD wget --spider -q http://localhost:8080/actuator/health || exit 1

# Usuário da aplicação
USER spring:spring

# Inicialização
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-Djava.security.egd=file:/dev/./urandom", "-Duser.timezone=America/Sao_Paulo", "-jar", "/app/app.jar"]