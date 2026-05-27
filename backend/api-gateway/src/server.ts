import Fastify from 'fastify';
import { registerCors } from './plugins/cors.ts';
import { registerClienteRoutes } from './routes/clientes.ts';
import { registerProxies } from './routes/proxy.ts';
import { registerHealthCheck } from './routes/health.ts';
import { registerRebootRoute } from './routes/reboot.ts';
import { registerErrorHandler } from './hooks/error-handler.ts';
import jwtPlugin from './plugins/jwt.ts';
import { registerAuthRoutes } from './routes/auth.ts';
import { env } from './config/env.ts';
import { registerClientByCpf } from './routes/composition/clientByCpf.ts';
import { registerCreateGerenteSaga } from './routes/composition/createGerenteSaga.ts';
import { registerGerentesComposition } from './routes/composition/gerentes.ts';
import { registerGerenteClientes } from './routes/composition/gerenteClientes.ts';

const gateway = Fastify({
  logger: true,
});

await registerCors(gateway);
await gateway.register(jwtPlugin);

// Aqui vão as rotas antes do proxy, como auth e composition
await registerAuthRoutes(gateway);
await registerClientByCpf(gateway);
await registerGerentesComposition(gateway);
await registerGerenteClientes(gateway);
await registerCreateGerenteSaga(gateway);
await registerClienteRoutes(gateway);

await registerProxies(gateway);
registerRebootRoute(gateway);
registerHealthCheck(gateway);
registerErrorHandler(gateway);

const PORT = Number(process.env.GATEWAY_PORT) || 3000;

try {
  await gateway.listen({ port: env.PORT, host: '0.0.0.0' });
  gateway.log.info(`API Gateway rodando em http://localhost:${env.PORT}`);
} catch (err) {
  gateway.log.error(err);
  process.exit(1);
}
