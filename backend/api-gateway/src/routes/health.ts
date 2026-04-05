import type { FastifyInstance } from 'fastify';

export function registerHealthCheck(gateway: FastifyInstance) {
  gateway.get('/health', async () => ({
    status: 'ok',
    timestamp: new Date().toISOString(),
    upstreams: {
      auth: process.env.AUTH_URL || 'http://localhost:8081',
      cliente: process.env.CLIENTE_URL || 'http://localhost:8082',
      conta: process.env.CONTA_URL || 'http://localhost:8083',
      gerente: process.env.GERENTE_URL || 'http://localhost:8084',
      admin: process.env.ADMIN_URL || 'http://localhost:8085',
    },
  }));
}
