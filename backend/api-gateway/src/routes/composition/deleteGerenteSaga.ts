import type { FastifyInstance } from 'fastify';
import { env } from '../../config/env.ts';
import { httpClient } from '../../services/http-client.ts';
import { buildUpstreamHeaders } from '../../hooks/upstream-headers.ts';
import { authorize } from '../../middlewares/authenticate.ts';

type CpfParam = { cpf: string };

type RemocaoGerenteSagaResponse = {
  cpf: string;
  gerenteDestinoCpf: string;
  contasReatribuidas: number;
};

export async function registerDeleteGerenteSaga(gateway: FastifyInstance) {
  gateway.delete<{
    Params: CpfParam;
    Reply: RemocaoGerenteSagaResponse;
  }>(
    '/gerentes/:cpf',
    { preHandler: authorize('ADMINISTRADOR') },
    async (request, reply) => {
      const { cpf } = request.params;
      const upstreamHeaders = buildUpstreamHeaders(request);

      const result = await httpClient.del<RemocaoGerenteSagaResponse>(
        `${env.upstreams.sagas}/sagas/gerentes/${cpf}`,
        upstreamHeaders,
      );

      return reply.code(200).send(result);
    },
  );
}