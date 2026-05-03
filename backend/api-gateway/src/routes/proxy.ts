import type { FastifyInstance } from 'fastify';
import proxy from '@fastify/http-proxy';
import { injectRequestId } from '../hooks/request-headers.ts';

type ProxyRoute = {
  upstream: string;
  prefix: string;
  rewritePrefix: string;
};

const proxyRoutes: ProxyRoute[] = [
  {
    upstream: process.env.AUTH_URL || 'http://localhost:8081',
    prefix: '/api/auth',
    rewritePrefix: '/',
  },
  {
    upstream: process.env.AUTH_URL || 'http://localhost:8081',
    prefix: '/login',
    rewritePrefix: '/login',
  },
  {
    upstream: process.env.CLIENTE_URL || 'http://localhost:8082',
    prefix: '/clientes',
    rewritePrefix: '/clientes',
  },
  {
    upstream: process.env.CONTA_URL || 'http://localhost:8083',
    prefix: '/api/contas',
    rewritePrefix: '/contas',
  },
  {
    upstream: process.env.GERENTE_URL || 'http://localhost:8084',
    prefix: '/api/gerentes',
    rewritePrefix: '/gerentes',
  },
  {
    upstream: process.env.ADMIN_URL || 'http://localhost:8085',
    prefix: '/api/admin',
    rewritePrefix: '/admin',
  },
];

export async function registerProxies(gateway: FastifyInstance) {
  for (const route of proxyRoutes) {
    await gateway.register(proxy, {
      upstream: route.upstream,
      prefix: route.prefix,
      rewritePrefix: route.rewritePrefix,
      replyOptions: {
        rewriteRequestHeaders: injectRequestId,
      },
    });
  }
}
