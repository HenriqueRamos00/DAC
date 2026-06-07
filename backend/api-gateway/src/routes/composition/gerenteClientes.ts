import type { FastifyInstance } from 'fastify';
import type { ClienteMsResponse, ClientResponseDto } from '../../types/dto/cliente.ts';
import type { ContaMsResponse } from '../../types/dto/conta.ts';
import { httpClient } from '../../services/http-client.ts';
import { env } from '../../config/env.ts';
import { buildUpstreamHeaders } from '../../hooks/upstream-headers.ts';
import { authorize } from '../../middlewares/authenticate.ts';

type GerenteCpfParams = {
  cpf: string;
};

export async function registerGerenteClientes(gateway: FastifyInstance) {
  gateway.get<{ Params: GerenteCpfParams; Reply: ClientResponseDto[] }>(
    '/gerentes/:cpf/clientes',
    { preHandler: authorize('GERENTE') },
    async (request) => {
      const { cpf } = request.params;
      const headers = buildUpstreamHeaders(request);

      const [contas, clientes] = await Promise.all([
        httpClient.get<ContaMsResponse[]>(
          `${env.upstreams.conta}/contas?gerenteCpf=${encodeURIComponent(cpf)}`,
          headers,
        ),
        httpClient.get<ClienteMsResponse[]>(
          `${env.upstreams.cliente}/clientes`,
          headers,
        ),
      ]);

      const clientesPorCpf = new Map(
        clientes.map((c) => [c.cpf, c]),
      );

      return contas
        .map((conta) => {
          const cliente = clientesPorCpf.get(conta.clienteCpf);
          if (!cliente) return null;

          return {
            cpf: cliente.cpf,
            nome: cliente.nome,
            telefone: cliente.telefone,
            email: cliente.email,
            cep: cliente.CEP,
            endereco: cliente.endereco,
            cidade: cliente.cidade,
            estado: cliente.estado,
            salario: cliente.salario,
            conta: conta.numeroConta,
            saldo: conta.saldo,
            limite: conta.limite,
            gerente: conta.gerenteCpf,
            gerente_nome: conta.gerenteNome,
            gerente_email: conta.gerenteEmail,
          };
        })
        .filter((item): item is ClientResponseDto => item !== null);
    },
  );
}
