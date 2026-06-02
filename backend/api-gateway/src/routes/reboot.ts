import type { FastifyInstance } from 'fastify';
import { env } from '../config/env.ts';

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
      auth: env.upstreams.auth,
      cliente: env.upstreams.cliente,
      conta: env.upstreams.conta,
      gerente: env.upstreams.gerente,
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
