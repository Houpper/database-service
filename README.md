# 🗄️ Database Service

O **Database Service** é responsável pelo gerenciamento e provisionamento da estrutura de banco de dados da aplicação.

O serviço utiliza **PostgreSQL** e **Flyway** para controlar as migrações dos bancos de dados, sendo responsável pela criação de schemas de tenants e pela execução das respectivas migrações de forma assíncrona.

Além disso, o serviço utiliza o **Netflix Eureka** para registro e descoberta dentro da arquitetura de serviços.

---

## 🚀 Funcionalidades

* Gerencia a estrutura do banco de dados da aplicação;
* Executa migrações utilizando **Flyway**;
* Mantém as migrações do schema de gerenciamento separadas das migrações dos tenants;
* Cria schemas específicos para os tenants;
* Provisiona o ambiente do tenant após a criação do schema;
* Executa migrações dos schemas de tenants de forma assíncrona;
* Utiliza eventos para desacoplar a criação do schema da execução das migrações;
* Registra o serviço no **Eureka Server**;
* Permite descoberta dinâmica do serviço dentro da arquitetura de serviços;
* Disponibiliza endpoints REST para gerenciamento dos schemas.

---

## 🧠 Como Funciona

O serviço possui dois contextos principais de migração:

### 🔹 Schema de Management

O schema `management` é utilizado pelo próprio serviço para armazenar estruturas necessárias ao gerenciamento do banco.

As migrations são carregadas a partir de:

```text
classpath:db/migration/management
````

Essa migração é executada automaticamente durante a inicialização da aplicação através do Flyway.

### 🔹 Schemas dos Tenants

Cada tenant possui seu próprio schema no PostgreSQL.

As migrations destinadas aos tenants são mantidas separadamente em:

```text
classpath:db/migration/tenant
```

Quando um novo schema é criado, o serviço publica um evento de migração:

```text
SchemaManagerService
        │
        ├── Criação do schema
        │
        └── Publicação do evento
                    │
                    ▼
          SchemaMigrationListener
                    │
                    ▼
          SchemaMigratorService
                    │
                    ▼
             Flyway Migration
```

A execução da migration do tenant ocorre de forma **assíncrona**, utilizando um executor dedicado.

---

## 🔄 Provisionamento de um Tenant

O provisionamento de um novo tenant segue o fluxo:

```text
1. Recebe a solicitação de criação
              │
              ▼
2. Cria o schema do tenant
              │
              ▼
3. Publica SchemaMigrationEvent
              │
              ▼
4. Transação realiza COMMIT
              │
              ▼
5. Listener recebe o evento AFTER_COMMIT
              │
              ▼
6. Migração é executada de forma assíncrona
              │
              ▼
7. Flyway aplica as migrations do tenant
```

O uso de `AFTER_COMMIT` garante que a migração somente seja iniciada após a confirmação da transação responsável pela criação do schema.

---

## 🌐 Endpoints

### Criar e provisionar schema

```http
POST /schema-manager/create
```

Exemplo de requisição:

```json
{
  "tenantID": "550e8400-e29b-41d4-a716-446655440000",
  "schemaName": "tenant_001"
}
```

A solicitação cria o schema e inicia o processo de provisionamento.

Resposta:

```http
202 Accepted
```

```json
{
  "message": "Solicitação de provisionamento aceita."
}
```

---

### Migrar schema

```http
POST /schema-manager/migrate
```

Exemplo:

```json
{
  "tenantID": "550e8400-e29b-41d4-a716-446655440000",
  "schemaName": "tenant_001"
}
```

A solicitação pública o evento responsável por iniciar a migração do schema.

Resposta:

```http
202 Accepted
```

```json
{
  "message": "Solicitação de migração aceita."
}
```

> A resposta `202 Accepted` indica que a solicitação foi aceita para processamento. A execução da migration ocorre de forma assíncrona.

---

## 🗂️ Estrutura das Migrations

As migrations são separadas por contexto:

```text
src/main/resources/
└── db/
    └── migration/
        ├── management/
        │   └── V1__...
        │
        └── tenant/
            └── V1__...
```

### Management

```yaml
spring:
  flyway:
    default-schema: management
    schemas:
      - management
    locations:
      - classpath:db/migration/management
```

### Tenant

As migrations dos tenants são executadas programaticamente pelo `SchemaMigratorService`, criando uma instância isolada
do Flyway para cada schema. Dessa forma, cada execução do Flyway fica direcionada ao schema informado.
---

## 🧩 Tecnologias Utilizadas

* [Java 25](https://www.oracle.com/java/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* [Spring Events](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html)
* [Spring Scheduling / Async](https://docs.spring.io/spring-framework/reference/integration/scheduling.html)
* [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)
* [Flyway](https://documentation.red-gate.com/flyway)
* [PostgreSQL](https://www.postgresql.org/)
* [Gradle](https://gradle.org/)
* [Docker](https://www.docker.com/)

---

## ⚙️ Configurações

A aplicação utiliza variáveis de ambiente para permitir diferentes configurações por ambiente.

Formato utilizado:

```text
${NOME_VARIAVEL:valor_padrao}
```

### 📋 Configurações disponíveis

| Variável      | Descrição                | Default                         |
| ------------- | ------------------------ | ------------------------------- |
| `DB_URL`      | Host do banco PostgreSQL | `localhost`                     |
| `DB_PORT`     | Porta do PostgreSQL      | `5432`                          |
| `DB_NAME`     | Nome do banco de dados   | `houpper`                       |
| `DB_USERNAME` | Usuário do banco         | `postgres`                      |
| `DB_PASSWORD` | Senha do banco           | `develop`                       |
| `EUREKA_URL`  | URL do Eureka Server     | `http://localhost:8761/eureka/` |

> Caso uma variável não seja definida, o valor padrão será utilizado automaticamente pela aplicação.

---

## 🔎 Eureka

O serviço é registrado automaticamente no Eureka utilizando:

```yaml
spring:
  application:
    name: database-service
```

A instância utiliza o endereço IP para comunicação entre os serviços:

```yaml
eureka:
  instance:
    prefer-ip-address: true
```

O identificador da instância é definido como:

```text
${spring.application.name}:${spring.cloud.client.ip-address}:${server.port}
```

O serviço também consulta o registro do Eureka para permitir a descoberta de outros serviços.

---

## 🛠️ Build Local

### 📦 Pré-requisitos

Antes de realizar o Build, certifique-se de possuir:

* [Java 25](https://www.oracle.com/java/)
* [Gradle](https://gradle.org/) ou o Gradle Wrapper incluído no projeto
* [PostgreSQL](https://www.postgresql.org/) ou uma instância PostgreSQL executando via Docker

### 🚀 Executando o Build

No diretório raiz do projeto:

```bash
./gradlew clean build
```

Ou utilizando uma instalação local do Gradle:

```bash
gradle clean build
```

### 📁 Artefato Gerado

Após o Build, o `.jar` será gerado em:

```text
/build/libs/database-service.jar
```

---

## 🐳 Docker

A aplicação pode ser executada em um container Docker juntamente com os demais serviços da arquitetura.

Exemplo:

```bash
docker run -d --name database-service --restart always -p 8801:8080 -e DB_URL=postgres -e DB_PORT=5432 -e DB_NAME=houpper -e DB_USERNAME=postgres -e DB_PASSWORD=develop -e EUREKA_URL=http://eureka-service:8761/eureka/ --network houpper-network database-service:dev
```

> O container PostgreSQL deve estar acessível através da rede Docker utilizada pela aplicação.

---

## 🩺 Health Check

O serviço utiliza o **Spring Boot Actuator** para disponibilizar informações de saúde da aplicação.

Endpoint:

```http
GET /actuator/health
```

Exemplo:

```text
http://localhost:8081/actuator/health
```

---

## 🔄 Integração com CI/CD

O serviço foi projetado para ser utilizado em pipelines de CI/CD.

O pipeline pode:

1. Executar o Build da aplicação;
2. Executar os testes;
3. Gerar o arquivo `.jar`;
4. Construir a imagem Docker;
5. Publicar a imagem em um registry;
6. Realizar o deploy no ambiente correspondente.

---

## 💡 Boas Práticas

* Mantenha migrations de `management` e `tenant` separadas;
* Não altere migrations já executadas em ambientes compartilhados;
* Crie uma nova migration para cada alteração estrutural;
* Utilize `202 Accepted` para operações cujo processamento ocorre de forma assíncrona;
* Evite executar migrations de tenants diretamente durante a inicialização da aplicação;
* Utilize o `SchemaMigrationEvent` para desacoplar o provisionamento da execução das migrations;
* Mantenha as credenciais do banco fora do código-fonte em ambientes de produção;
* Utilize variáveis de ambiente para configurações específicas de cada ambiente.

---

## 📌 Status do Projeto

🚧 **Em desenvolvimento.**

Funcionalidades atualmente implementadas:

* [x] Configuração do PostgreSQL
* [x] Integração com Flyway
* [x] Migrations do schema `management`
* [x] Criação de schemas de tenants
* [x] Migrations específicas para tenants
* [x] Provisionamento assíncrono de schemas
* [x] Eventos para disparo das migrations
* [x] Integração com Eureka
* [x] API REST para gerenciamento de schemas
* [x] Execução assíncrona das migrations
* [x] Configuração para execução via Docker
* [ ] Evolução do gerenciamento de configurações
* [ ] Monitoramento do status das migrations
* [ ] Integração completa com CI/CD

---

## 🚀 Próximos Passos

O serviço continuará evoluindo para centralizar as responsabilidades relacionadas ao gerenciamento da estrutura de banco de dados da aplicação, incluindo o acompanhamento das operações de provisionamento e migração dos tenants.

```