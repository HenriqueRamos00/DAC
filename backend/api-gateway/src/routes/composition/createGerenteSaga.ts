import type { FastifyInstance } from 'fastify';
import { env } from '../../config/env.ts';
import { httpClient } from '../../services/http-client.ts';
import { buildUpstreamHeaders } from '../../hooks/upstream-headers.ts';
import { authorize } from '../../middlewares/authenticate.ts';
import type {
  InserirGerenteRequest,
  InserirGerenteSagaResponse,
} from '../../types/dto/gerente.ts';

export async function registerCreateGerenteSaga(gateway: FastifyInstance) {
  gateway.post<{
    Body: InserirGerenteRequest;
    Reply: InserirGerenteSagaResponse;
  }>(
    '/gerentes',
    { preHandler: authorize('ADMINISTRADOR') },
    async (request, reply) => {
      const upstreamHeaders = buildUpstreamHeaders(request);

      const result = await httpClient.post<InserirGerenteSagaResponse>(
        `${env.upstreams.sagas}/sagas/gerentes`,
        request.body,
        upstreamHeaders,
      );

      return reply.code(201).send(result);
    },
  );
}