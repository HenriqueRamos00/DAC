import type { FastifyInstance } from 'fastify';
import { env } from '../config/env.ts';
import { buildUpstreamHeaders } from '../hooks/upstream-headers.ts';
import { authenticate, authorize } from '../middlewares/authenticate.ts';
import { httpClient } from '../services/http-client.ts';

type ClienteParams = {
  cpf: string;
};

type RejeitarClienteBody = {
  motivo: string;
};

function buildClientesUrl(req: string): string {
  const queryIndex = req.indexOf('?');
  const query = queryIndex >= 0 ? req.slice(queryIndex) : '';

  return `${env.upstreams.cliente}/clientes${query}`;
}

export async function registerClienteRoutes(gateway: FastifyInstance) {
  gateway.post<{ Body: unknown }>('/clientes', async (request, reply) => {
    const response = await httpClient.post<unknown>(
      `${env.upstreams.cliente}/clientes`,
      request.body,
      buildUpstreamHeaders(request),
    );

    return reply.code(201).send(response);
  });

  gateway.get('/clientes', { preHandler: authenticate }, async (request, reply) => {
    const response = await httpClient.get<unknown>(
      buildClientesUrl(request.raw.url ?? request.url),
      buildUpstreamHeaders(request),
    );

    return reply.code(200).send(response);
  });

  gateway.post<{ Params: ClienteParams }>(
    '/clientes/:cpf/aprovar',
    {
      preHandler: authorize('GERENTE'),
    },
    async (request, reply) => {
      const response = await httpClient.post<unknown>(
        `${env.upstreams.cliente}/clientes/${request.params.cpf}/aprovar`,
        {},
        buildUpstreamHeaders(request),
      );

      return reply.code(200).send(response);
    },
  );

  gateway.post<{ Params: ClienteParams; Body: RejeitarClienteBody }>(
    '/clientes/:cpf/rejeitar',
    {
      preHandler: authorize('GERENTE'),
    },
    async (request, reply) => {
      const response = await httpClient.post<unknown>(
        `${env.upstreams.cliente}/clientes/${request.params.cpf}/rejeitar`,
        request.body,
        buildUpstreamHeaders(request),
      );

      return reply.code(200).send(response);
    },
  );

  gateway.put<{ Params: ClienteParams; Body: unknown }>(
    '/clientes/:cpf',
    {
      preHandler: authenticate,
    },
    async (request, reply) => {
      const response = await httpClient.put<unknown>(
        `${env.upstreams.cliente}/clientes/${request.params.cpf}`,
        request.body,
        buildUpstreamHeaders(request),
      );

      return reply.code(200).send(response);
    },
  );
}
