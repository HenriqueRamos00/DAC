import Fastify from 'fastify';
import { registerCors } from './plugins/cors.ts';
import { registerProxies } from './routes/proxy.ts';
import { registerHealthCheck } from './routes/health.ts';
import { registerRebootRoute } from './routes/reboot.ts';
import { registerErrorHandler } from './hooks/error-handler.ts';

const gateway = Fastify({
  logger: true,
});

await registerCors(gateway);
await registerProxies(gateway);
registerRebootRoute(gateway);
registerHealthCheck(gateway);
registerErrorHandler(gateway);

const PORT = Number(process.env.GATEWAY_PORT) || 3000;

try {
  await gateway.listen({ port: PORT, host: '0.0.0.0' });
  gateway.log.info(`API Gateway rodando em http://localhost:${PORT}`);
} catch (err) {
  gateway.log.error(err);
  process.exit(1);
}
