import type { FastifyInstance } from 'fastify';
import { env } from '../../config/env.ts';
import { buildUpstreamHeaders } from '../../hooks/upstream-headers.ts';
import { authorize } from '../../middlewares/authenticate.ts';
import { httpClient } from '../../services/http-client.ts';
import type {
  ClienteDashboardResponse,
  ContaGerenteMsResponse,
  GerenteDashboardResponse,
  GerenteMsResponse,
  ResumoContasGerenteMsResponse,
} from '../../types/dto/gerente.ts';

type GerentesQuery = {
  filtro?: string;
};

type GerenteCpfParams = {
  cpf: string;
};

export async function registerGerentesComposition(gateway: FastifyInstance) {
  gateway.get<{ Querystring: GerentesQuery }>(
    '/gerentes',
    { preHandler: authorize('ADMINISTRADOR') },
    async (request) => {
      const headers = buildUpstreamHeaders(request);
      const gerentes = await httpClient.get<GerenteMsResponse[]>(
        `${env.upstreams.gerente}/gerentes`,
        headers,
      );

      if (request.query.filtro !== 'dashboard') {
        return gerentes;
      }

      const resumosContas = await httpClient.get<ResumoContasGerenteMsResponse[]>(
        `${env.upstreams.conta}/contas/resumo-gerentes`,
        headers,
      );

      return montarDashboard(gerentes, resumosContas);
    },
  );

  gateway.get<{ Params: GerenteCpfParams }>(
    '/gerentes/:cpf',
    { preHandler: authorize('ADMINISTRADOR') },
    async (request) => {
      const headers = buildUpstreamHeaders(request);
      const cpf = encodeURIComponent(request.params.cpf);

      return httpClient.get<GerenteMsResponse>(
        `${env.upstreams.gerente}/gerentes/${cpf}`,
        headers,
      );
    },
  );
}

function montarDashboard(
  gerentes: GerenteMsResponse[],
  resumosContas: ResumoContasGerenteMsResponse[],
): GerenteDashboardResponse[] {
  const resumosPorGerente = new Map(
    resumosContas.map((resumo) => [resumo.gerenteCpf, resumo]),
  );

  return gerentes
    .map((gerente) => montarResumoGerente(gerente, resumosPorGerente.get(gerente.cpf)))
    .sort(compararPorSaldoPositivo);
}

function montarResumoGerente(
  gerente: GerenteMsResponse,
  resumoContas?: ResumoContasGerenteMsResponse,
): GerenteDashboardResponse {
  const contas = resumoContas?.clientes ?? [];

  return {
    gerente: {
      cpf: gerente.cpf,
      nome: gerente.nome,
      email: gerente.email,
      tipo: 'GERENTE',
    },
    clientes: contas.map(toClienteDashboard),
    saldo_positivo: resumoContas?.saldoPositivo ?? 0,
    saldo_negativo: resumoContas?.saldoNegativo ?? 0,
  };
}

function toClienteDashboard(conta: ContaGerenteMsResponse): ClienteDashboardResponse {
  return {
    cliente: conta.clienteCpf,
    numero: conta.numeroConta,
    saldo: conta.saldo,
    limite: conta.limite,
    gerente: conta.gerenteCpf,
    criacao: conta.dataCriacao,
  };
}

function compararPorSaldoPositivo(
  left: GerenteDashboardResponse,
  right: GerenteDashboardResponse,
) {
  return right.saldo_positivo - left.saldo_positivo;
}
