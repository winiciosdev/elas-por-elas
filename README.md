# Rede de Apoio — Backend

API REST em Java 21, Spring Boot 3.5, Maven, PostgreSQL, Spring Security e JWT.

## Requisitos
- Java 21
- Maven 3.9+
- Docker Engine e Docker Compose

## 1. Clonar o projeto e preparar o ambiente local

```bash
git clone <url-do-repositorio>
cd rede-apoio
cp .env.example .env
```

Edite o arquivo `.env` e ajuste os valores de:
- `POSTGRES_PASSWORD`
- `DB_PASSWORD`
- `JWT_SECRET`

Exemplo:

```dotenv
POSTGRES_DB=rede_apoio
POSTGRES_USER=rede_apoio_app
POSTGRES_PASSWORD=sua_senha_forte
POSTGRES_PORT=5433
DB_URL=jdbc:postgresql://localhost:5433/rede_apoio
DB_USERNAME=rede_apoio_app
DB_PASSWORD=sua_senha_forte
JWT_SECRET=BASE64_SECRET_WITH_AT_LEAST_32_BYTES_CHANGE_THIS_VALUE
JWT_EXPIRATION_MINUTES=60
```

`JWT_SECRET` deve ser uma chave Base64 válida com pelo menos 32 bytes. Gere uma com:

```bash
openssl rand -base64 32
```

O projeto usa `spring-dotenv`, então o arquivo `.env` é carregado automaticamente no startup do Spring Boot. O Docker Compose também lê `.env` automaticamente.

## 2. Subir o PostgreSQL

```bash
docker compose up -d
docker compose ps
```

## 3. Iniciar a API

Sem precisar executar `source .env` nem export manual:

```bash
mvn clean verify
mvn spring-boot:run
```

A aplicação aplica automaticamente as migrations Flyway no primeiro startup.

## 4. Endpoints úteis

- Health: `GET http://localhost:8080/api/v1/health`
- Swagger: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## 5. Cadastro e login

Cadastro:

```bash
curl -X POST http://localhost:8080/api/v1/auth/cadastro \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Maria Exemplo","email":"teste@example.com","senha":"SenhaForte123","cidadeUf":"Recife, PE"}'
```

Login:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"teste@example.com","senha":"SenhaForte123"}'
```

Copie o token retornado e envie `Authorization: Bearer SEU_TOKEN` para rotas autenticadas.

## 6. Endpoints implementados

### Autenticação e conta
- `POST /api/v1/auth/cadastro`
- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`
- `PATCH /api/v1/auth/me`

### Unidades de apoio
- `GET /api/v1/unidades?cidade=Recife&bairro=Boa Vista`
- `GET /api/v1/unidades/{id}`
- `POST /api/v1/unidades` (ADMIN)
- `PUT /api/v1/unidades/{id}` (ADMIN)
- `DELETE /api/v1/unidades/{id}` (ADMIN)
- `POST /api/v1/unidades/{id}/verificacao` (ADMIN)

### Tipos de serviço
- `GET /api/v1/tipos-servico`
- `GET /api/v1/tipos-servico/{id}`
- `POST /api/v1/tipos-servico` (ADMIN)
- `PUT /api/v1/tipos-servico/{id}` (ADMIN)
- `DELETE /api/v1/tipos-servico/{id}` (ADMIN)

### Profissionais voluntárias
- `GET /api/v1/profissionais?cidadeUf=Recife`
- `GET /api/v1/profissionais/{id}`
- `POST /api/v1/profissionais/me`
- `PUT /api/v1/profissionais/me`
- `DELETE /api/v1/profissionais/me` (desativa perfil)
- `POST /api/v1/profissionais/{id}/verificacao` (ADMIN)

### Meu Apoio
- `GET /api/v1/meu-apoio`
- `POST /api/v1/meu-apoio`
- `DELETE /api/v1/meu-apoio/{id}`

Exemplo de favorito de unidade:
```json
{"unidadeId":"UUID-DA-UNIDADE","profissionalId":null}
```
Exemplo de favorito de profissional:
```json
{"unidadeId":null,"profissionalId":"UUID-DA-PROFISSIONAL"}
```

## 7. Criar o primeiro administrador

Por segurança, o cadastro público sempre cria usuários com role `USER`.
Após registrar sua conta, promova-a manualmente no banco local:

```bash
docker compose exec postgres psql -U rede_apoio_app -d rede_apoio
```

```sql
UPDATE usuaria SET role = 'ADMIN' WHERE email = 'seu-email@example.com';
```

Saia com `\q` e faça login novamente para obter um token com a role atualizada.

## Observações importantes

- Não faça commit do arquivo `.env`.
- Use HTTPS em ambientes publicados e segredos gerenciados no ambiente de deploy.
- O projeto é uma base MVP; antes de produção, adicione rate limiting no login, política de recuperação de senha, auditoria de ações administrativas, paginação, logs estruturados e testes de integração completos.
- A verificação de credenciais e unidades exige ADMIN.
- `GET /api/v1/health` verifica apenas que a aplicação responde; não é uma verificação profunda de dependências.

## Modelo normalizado atualizado
A migration cria também `endereco` (CEP, logradouro, bairro, cidade/UF e coordenadas), `profissional_servico`, colunas de estado de moderação e as chaves estrangeiras opcionais/obrigatórias previstas no modelo. O MVP preserva temporariamente alguns campos antigos de cidade/endereço utilizados pelos endpoints existentes para facilitar a transição; use `endereco_id` como referência normalizada nos novos fluxos. `MEU_APOIO` tem CHECK para garantir que apenas um alvo seja preenchido.
