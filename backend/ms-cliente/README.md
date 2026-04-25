# ms-cliente

Microsserviço responsável pelo subdomínio de cliente no BANTADS.

Hoje ele cobre:
- autocadastro síncrono via HTTP, criando cliente com status `PENDENTE`
- consulta de clientes
- listagem de clientes pendentes para aprovação
- aprovação local via HTTP
- aprovação via RabbitMQ para uso pela saga
- rejeição local via HTTP com motivo
- rejeição via RabbitMQ para uso pela saga

## Requisitos

- Java 21
- Maven Wrapper (`./mvnw`)
- PostgreSQL e RabbitMQ disponíveis
- variáveis do projeto carregadas a partir do `.env` da raiz

## Configuração

O serviço lê as variáveis do `.env` da raiz do projeto.

Principais variáveis usadas:
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `POSTGRES_DB`
- `POSTGRES_PORT`
- `DB_HOST`
- `RABBITMQ_DEFAULT_USER`
- `RABBITMQ_DEFAULT_PASS`
- `RABBITMQ_PORT`
- `RABBITMQ_HOST`
- `SERVER_PORT`
- `SAGA_RABBITMQ_EXCHANGE`
- `SAGA_RABBITMQ_QUEUE_CLIENTE_APROVAR_COMMAND`
- `SAGA_RABBITMQ_ROUTING_KEY_CLIENTE_APROVAR_COMMAND`
- `SAGA_RABBITMQ_ROUTING_KEY_CLIENTE_APROVAR_SUCESSO`
- `SAGA_RABBITMQ_ROUTING_KEY_CLIENTE_APROVAR_FALHA`
- `SAGA_RABBITMQ_QUEUE_CLIENTE_REJEITAR_COMMAND`
- `SAGA_RABBITMQ_ROUTING_KEY_CLIENTE_REJEITAR_COMMAND`
- `SAGA_RABBITMQ_ROUTING_KEY_CLIENTE_REJEITAR_SUCESSO`
- `SAGA_RABBITMQ_ROUTING_KEY_CLIENTE_REJEITAR_FALHA`

## Rodando localmente

Suba a infra na raiz do projeto:

```bash
docker compose up -d postgres-db rabbitmq
```

Carregue as variáveis e rode o serviço:

```bash
cd backend/ms-cliente
set -a
source ../../.env
set +a
./mvnw spring-boot:run
```

O serviço sobe em:

```text
http://localhost:8082
```

## Rodando via Docker

Na raiz do projeto:

```bash
docker compose up postgres-db ms-cliente
```

## Endpoints HTTP

### `POST /clientes`

Cria um cliente pendente.

Exemplo de request:

```json
{
  "cpf": "12345678901",
  "nome": "Joao da Silva",
  "email": "joao@bantads.com.br",
  "telefone": "41999999999",
  "salario": 3500.00,
  "cep": "80020310",
  "logradouro": "Rua XV de Novembro",
  "cidade": "Curitiba",
  "estado": "PR",
  "complemento": "Apto 101",
  "numero": "100"
}
```

Resposta esperada:
- `201 Created`
- `409 Conflict` para CPF, e-mail ou telefone duplicado
- `400 Bad Request` para erro de validação


### `GET /clientes`

Lista clientes aprovados.

```bash
curl http://localhost:8082/clientes
```

### `GET /clientes?filtro=para_aprovar`

Lista clientes pendentes.

```bash
curl "http://localhost:8082/clientes?filtro=para_aprovar"
```

### `GET /clientes/{cpf}`

Consulta um cliente por CPF.

```bash
curl http://localhost:8082/clientes/12345678901
```

### `PATCH /clientes/{cpf}/aprovar`

Aprova um cliente pendente via HTTP.

```bash
curl -X PATCH http://localhost:8082/clientes/12345678901/aprovar
```

### `PATCH /clientes/{cpf}/rejeitar`

Rejeita um cliente pendente via HTTP, exigindo um motivo.

```bash
curl -X PATCH http://localhost:8082/clientes/12345678901/rejeitar \
  -H "Content-Type: application/json" \
  -d '{"motivo":"Renda incompatível com a política do banco"}'
```

## Aprovação via RabbitMQ

O `ms-cliente` consome comandos da fila:

```text
cliente.aprovar.command
```

Binding esperado:
- exchange: `bantads.saga`
- routing key: `cliente.aprovar`

Payload de entrada:

```json
{
  "cpf": "12345678901"
}
```

Em caso de sucesso, o serviço publica:
- routing key: `cliente.aprovado`

Payload de sucesso:

```json
{
  "codigo": 1,
  "cpf": "12345678901",
  "email": "teste@bantads.com",
  "status": "APROVADO",
  "dataAprovacao": "2026-04-25T19:30:00"
}
```

Em caso de falha de negócio, publica:
- routing key: `cliente.aprovacao.falhou`

Payload de falha:

```json
{
  "cpf": "12345678901",
  "motivo": "Cliente nao encontrado para o CPF 12345678901"
}
```

## Rejeição via RabbitMQ

O `ms-cliente` também consome comandos da fila:

```text
cliente.rejeitar.command
```

Binding esperado:
- exchange: `bantads.saga`
- routing key: `cliente.rejeitar`

Payload de entrada:

```json
{
  "cpf": "12345678901",
  "motivo": "Renda incompatível com a política do banco"
}
```

Em caso de sucesso, o serviço publica:
- routing key: `cliente.rejeitado`

Payload de sucesso:

```json
{
  "codigo": 1,
  "cpf": "12345678901",
  "email": "teste@bantads.com",
  "status": "REJEITADO",
  "motivoRejeicao": "Renda incompatível com a política do banco",
  "dataReprovacao": "2026-04-25T20:10:00"
}
```

Em caso de falha de negócio, publica:
- routing key: `cliente.rejeicao.falhou`

Payload de falha:

```json
{
  "cpf": "12345678901",
  "motivo": "Cliente nao encontrado para o CPF 12345678901"
}
```
