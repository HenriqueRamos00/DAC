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
    upstream: process.env.CLIENTE_URL || 'http://localhost:8082',
    prefix: '/clientes',
    rewritePrefix: '/clientes',
    protected: true,
  },
  {
    upstream: process.env.CONTA_URL || 'http://localhost:8083',
    prefix: '/contas',
    rewritePrefix: '/contas',
    protected: true,
  },
  {
    upstream: process.env.GERENTE_URL || 'http://localhost:8084',
    prefix: '/gerentes',
    rewritePrefix: '/gerentes',
    protected: true,
  },
  {
    upstream: process.env.ADMIN_URL || 'http://localhost:8085',
    prefix: '/admin',
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
