import type { FastifyInstance } from 'fastify';
import proxy from '@fastify/http-proxy';
import { injectRequestId } from '../hooks/request-headers.ts';
import { authenticate, authorize } from '../middlewares/authenticate.ts';

type ProxyRoute = {
  upstream: string;
  prefix: string;
  rewritePrefix: string;
  protected?: boolean;
  adminOnly?: boolean;
};

const proxyRoutes: ProxyRoute[] = [
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
    adminOnly: true,
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
    let preHandler;

    if (route.adminOnly) {
      preHandler = authorize('ADMINISTRADOR');
    } else if (route.protected) {
      preHandler = authenticate;
    }

    await gateway.register(proxy, {
      upstream: route.upstream,
      prefix: route.prefix,
      rewritePrefix: route.rewritePrefix,
      preHandler,
      replyOptions: {
        rewriteRequestHeaders: injectRequestId,
      },
    });
  }
}
