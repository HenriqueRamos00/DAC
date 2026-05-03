# api-gateway

API Gateway do BANTADS usando Fastify.

Ele é o ponto único de entrada da aplicação e encaminha as chamadas HTTP para os microsserviços do backend.

## Rotas já encaminhadas

- `/login` -> `ms-auth`
- `/clientes` -> `ms-cliente`
- `/contas` -> `ms-conta`
- `/gerentes` -> `ms-gerente`
- `/admin` -> `ms-admin`
- `/reboot` -> rota do gateway para acionar o reset dos serviços que implementarem `/reboot`

## Rodando localmente

```bash
cd backend/api-gateway
npm install
npm run dev
```

Por padrão, o gateway sobe em:

```text
http://localhost:3000
```

## Rodando via Docker Compose

Na raiz do projeto:

```bash
docker compose up --build api-gateway
```

## Variáveis de ambiente

- `GATEWAY_PORT`
- `CLIENTE_URL`
- `AUTH_URL`
- `CONTA_URL`
- `GERENTE_URL`
- `ADMIN_URL`

No ambiente Docker, essas URLs devem apontar para os nomes dos serviços na rede do Compose, por exemplo:

```text
CLIENTE_URL=http://ms-cliente:8082
```
