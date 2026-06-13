import type { FastifyInstance } from 'fastify';
import { env } from '../config/env.ts';
import { buildUpstreamHeaders } from '../hooks/upstream-headers.ts';
import { authenticate, authorize } from '../middlewares/authenticate.ts';
import { httpClient } from '../services/http-client.ts';
import type { ClienteGerenteResponseDto, ClienteMsResponse, ClientResponseDto } from '../types/dto/cliente.ts';
import type { ContaMsResponse, ResumoContasGerenteMsResponse } from '../types/dto/conta.ts';

type ClienteParams = {
  cpf: string;
};

type ClientesQuery = {
  filtro?: string;
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

  gateway.get<{ Querystring: ClientesQuery }>('/clientes', { preHandler: authenticate }, async (request, reply) => {
    if (isRelatorioClientes(request.query)) {
      await authorize('ADMINISTRADOR')(request, reply);

      const response = await getClientesRelatorio(buildUpstreamHeaders(request));

      return reply.code(200).send(response);
    }

    if (isMelhoresClientes(request.query)) {
      await authorize('GERENTE')(request, reply);
      const todos = await getClientesRelatorio(buildUpstreamHeaders(request));
      const top3 = todos
        .sort((a, b) => b.saldo - a.saldo)
        .slice(0, 3);
      return reply.code(200).send(top3);
    }

    if (!request.query.filtro) {
      await authorize('GERENTE')(request, reply);

      const response = await getClientesDoGerente(
        request.user.sub,
        buildUpstreamHeaders(request),
      );

      return reply.code(200).send(response);
    }

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
      const headers = buildUpstreamHeaders(request);
      const cpf = encodeURIComponent(request.params.cpf);

      const response = await httpClient.post<unknown>(
        `${env.upstreams.sagas}/sagas/clientes/${cpf}/aprovar`,
        {},
        headers,
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
        `${env.upstreams.sagas}/sagas/clientes/${request.params.cpf}/perfil`,
        request.body,
        buildUpstreamHeaders(request),
      );

      return reply.code(200).send(response);
    },
  );
}

function isRelatorioClientes(query: ClientesQuery): boolean {
  return query.filtro === 'adm_relatorio_clientes';
}

function isMelhoresClientes(query: ClientesQuery): boolean {
  return query.filtro === 'melhores_clientes';
}

function isParaAprovar(query: ClientesQuery): boolean {
  return query.filtro === 'para_aprovar';
}

async function getClientesDoGerente(
  gerenteCpf: string,
  headers: Record<string, string>,
): Promise<ClienteGerenteResponseDto[]> {
  const [contas, clientes] = await Promise.all([
    httpClient.get<ContaMsResponse[]>(
      `${env.upstreams.conta}/contas?gerenteCpf=${encodeURIComponent(gerenteCpf)}`,
      headers,
    ),
    httpClient.get<ClienteMsResponse[]>(
      `${env.upstreams.cliente}/clientes`,
      headers,
    ),
  ]);

  const clientesPorCpf = new Map(
    clientes.map((cliente) => [cliente.cpf, cliente]),
  );

  return contas
    .map((conta) => {
      const cliente = clientesPorCpf.get(conta.clienteCpf);
      if (!cliente) return null;

      return {
        cpf: cliente.cpf,
        nome: cliente.nome,
        email: cliente.email,
        telefone: cliente.telefone,
        endereco: cliente.endereco,
        cidade: cliente.cidade,
        estado: cliente.estado,
        conta: conta.numeroConta,
        saldo: conta.saldo,
        limite: conta.limite,
      };
    })
    .filter((cliente): cliente is ClienteGerenteResponseDto => cliente !== null);
}

async function getClientesRelatorio(
  headers: Record<string, string>,
): Promise<ClientResponseDto[]> {
  const [clientes, resumosContas] = await Promise.all([
    httpClient.get<ClienteMsResponse[]>(
      `${env.upstreams.cliente}/clientes`,
      headers,
    ),
    httpClient.get<ResumoContasGerenteMsResponse[]>(
      `${env.upstreams.conta}/contas/resumo-gerentes`,
      headers,
    ),
  ]);

  const contasPorCpf = new Map(
    resumosContas
      .flatMap((resumo) => resumo.clientes)
      .map((conta) => [conta.clienteCpf, conta]),
  );

  return clientes.map((cliente) => {
    const conta = contasPorCpf.get(cliente.cpf);

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
      conta: conta?.numeroConta ?? '',
      saldo: conta?.saldo ?? 0,
      limite: conta?.limite ?? 0,
      gerente: conta?.gerenteCpf ?? '',
      gerente_nome: conta?.gerenteNome ?? '',
      gerente_email: conta?.gerenteEmail ?? '',
    };
  });
}
