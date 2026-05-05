import type { FastifyInstance } from 'fastify';

async function callReboot(upstream: string) {
  try {
    const response = await fetch(`${upstream}/reboot`);

    return {
      ok: response.ok,
      status: response.status,
    };
  } catch {
    return {
      ok: false,
      status: 0,
    };
  }
}

export function registerRebootRoute(gateway: FastifyInstance) {
  gateway.get('/reboot', async (_request, reply) => {
    const upstreams = {
      auth: process.env.AUTH_URL || 'http://localhost:8081',
      cliente: process.env.CLIENTE_URL || 'http://localhost:8082',
      conta: process.env.CONTA_URL || 'http://localhost:8083',
      gerente: process.env.GERENTE_URL || 'http://localhost:8084',
      admin: process.env.ADMIN_URL || 'http://localhost:8085',
    };

    const results = await Promise.all(
      Object.entries(upstreams).map(async ([service, upstream]) => [
        service,
        await callReboot(upstream),
      ]),
    );

    return reply.code(200).send({
      status: 'ok',
      services: Object.fromEntries(results),
    });
  });
}
