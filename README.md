# DAC
Trabalho de DAC TADS UFPR

## Frontend
```bash
cd frontend/
```

Para baixar as dependências (node_modules) use:
```bash
npm i
```

Para iniciar o frontend use:
```bash
npm run dev
```
Acesse em:
```bash
http://localhost:5173/
```

## Backend (json server)

```sh
cd backend/mock/
```

```sh
chmod +x ./start.sh
```

```sh
./start.sh
```
## Backend (database)

Subir infraestrutura
```sh
docker compose up -d postgres-db mongo-db rabbitmq
```

Resetar bancos (re-seed)
```sh
docker compose down -v
docker compose up -d postgres-db mongo-db rabbitmq
```

Verificar PostgreSQL
```sh
docker compose exec postgres-db psql -U postgres_user -d bantads
```

Verificar MongoDB
```sh
docker compose exec mongo-db mongosh bantads_auth
```

RabbitMQ UI
http://localhost:15672 (bantads/bantads)
