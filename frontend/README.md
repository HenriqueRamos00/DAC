# Frontend

Frontend da aplicação DAC, construído com React Router, React 19, TypeScript e Tailwind CSS.

## Requisitos

- Node.js
- npm

## Executando localmente

Instale as dependências:

```bash
npm install
```

Inicie o servidor de desenvolvimento:

```bash
npm run dev
```

A aplicação ficará disponível em `http://localhost:5173`.

Se o frontend precisar se comunicar com o API Gateway rodando localmente, use a variável abaixo:

```txt
API_URL=http://localhost:3000
```

## Executando com Docker

No diretório `frontend`, suba o ambiente com:

```bash
docker compose up
```

Nesse modo, o container já usa a configuração abaixo para acessar o backend exposto na máquina host:

```txt
API_URL=http://host.docker.internal:3000
```

Após subir os containers, acesse `http://localhost:5173`.

### Observações para Ubuntu 20.04

No Ubuntu 20.04, o `wrangler` não é suportado no fluxo adotado por este projeto. Por isso, nesse ambiente, a forma recomendada de executar o frontend é com Docker.

Se estiver usando Ubuntu 20.04, confira se o comando abaixo funciona:

```bash
docker compose version
```

Se apenas `docker-compose` existir na sua máquina, instale ou habilite o plugin mais recente do Docker antes de subir o frontend.

Se aparecer erro de permissão ao executar comandos Docker, adicione seu usuário ao grupo `docker`:

```bash
sudo usermod -aG docker $USER
```

Depois disso, encerre a sessão e entre novamente no sistema antes de testar outra vez.

Se o frontend em Docker não conseguir acessar o backend da máquina host, confirme se o `docker-compose.yaml` está mantendo:

```txt
host.docker.internal:host-gateway
```

Esse mapeamento é importante para o `API_URL=http://host.docker.internal:3000` funcionar corretamente no Linux.

## Scripts úteis

```bash
npm run dev
npm run build
npm run preview
npm run typecheck
```

## Build e deploy

O projeto possui configuração de deploy com Cloudflare Workers via Wrangler.

Para gerar a build de produção:

```bash
npm run build
```

Para publicar:

```bash
npm run deploy
```
