import Fastify from 'fastify';
import { registerCors } from './plugins/cors.ts';
import { registerProxies } from './routes/proxy.ts';
import { registerHealthCheck } from './routes/health.ts';
import { registerErrorHandler } from './hooks/error-handler.ts';
import jwtPlugin from './plugins/jwt.ts';
import { registerAuthRoutes } from './routes/auth.ts';
import { env } from './config/env.ts';

const gateway = Fastify({
  logger: true,
});

await registerCors(gateway);
await gateway.register(jwtPlugin);

// Aqui vão as rotas antes do proxy, como auth e composition
await registerAuthRoutes(gateway);

await registerProxies(gateway);
//registerHealthCheck(gateway);
registerErrorHandler(gateway);

const PORT = Number(process.env.GATEWAY_PORT) || 3000;

try {
  await gateway.listen({ port: env.PORT, host: '0.0.0.0' });
  gateway.log.info(`API Gateway rodando em http://localhost:${env.PORT}`);
} catch (err) {
  gateway.log.error(err);
  process.exit(1);
}
