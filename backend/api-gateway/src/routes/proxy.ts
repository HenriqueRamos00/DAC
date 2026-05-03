import type { FastifyInstance } from 'fastify';
import proxy from '@fastify/http-proxy';
import { injectRequestId } from '../hooks/request-headers.ts';
import { authenticate } from '../middlewares/authenticate.ts';

type ProxyRoute = {
  upstream: string;
  prefix: string;
  rewritePrefix: string;
  protected?: boolean;
};

const proxyRoutes: ProxyRoute[] = [
  {
    upstream: process.env.AUTH_URL || 'http://localhost:8081',
    prefix: '/api/auth',
    rewritePrefix: '/',
    protected: true,
  },
  {
    upstream: process.env.CLIENTE_URL || 'http://localhost:8082',
    prefix: '/api/clientes',
    rewritePrefix: '/clientes',
    protected: true,
  },
  {
    upstream: process.env.CONTA_URL || 'http://localhost:8083',
    prefix: '/api/contas',
    rewritePrefix: '/contas',
    protected: true,
  },
  {
    upstream: process.env.GERENTE_URL || 'http://localhost:8084',
    prefix: '/api/gerentes',
    rewritePrefix: '/gerentes',
    protected: true,
  },
  {
    upstream: process.env.ADMIN_URL || 'http://localhost:8085',
    prefix: '/api/admin',
    rewritePrefix: '/admin',
    protected: true,
  },
];

export async function registerProxies(gateway: FastifyInstance) {
  for (const route of proxyRoutes) {
    await gateway.register(proxy, {
      upstream: route.upstream,
      prefix: route.prefix,
      rewritePrefix: route.rewritePrefix,
      preHandler: route.protected ? authenticate : undefined,
      replyOptions: {
        rewriteRequestHeaders: injectRequestId,
      },
    });
  }
}
