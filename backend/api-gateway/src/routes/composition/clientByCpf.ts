import type { FastifyInstance } from "fastify";
import type { ClienteMsResponse, ClientResponseDto } from "../../types/dto/cliente.ts";
import { httpClient } from "../../services/http-client.ts";
import { env } from "../../config/env.ts";
import type { ContaMsResponse } from "../../types/dto/conta.ts";
import type { GerenteMsResponse } from "../../types/dto/gerente.ts";
import { authenticate } from "../../middlewares/authenticate.ts";
import { ForbiddenError, UnauthorizedError } from "../../hooks/errors.ts";

type ClientCpf  = {
    cpf: string
};

export async function registerClientByCpf(gateway: FastifyInstance) {
 
    gateway.get<{
        Params: ClientCpf
        Reply: ClientResponseDto
    }>('/clientes/:cpf', { preHandler: authenticate }, async (request, reply) => {

        const { cpf } = request.params;
        const claims = request.user;
        const upstreamHeaders = {
            'x-request-id': request.id,
            'x-user-id': claims.sub,
            'x-user-role': claims.role,
            'x-user-email': claims.email,
        };

        if (claims.role == "CLIENTE" && claims.sub != cpf) {
            throw new ForbiddenError('O usuário não tem permissão para efetuar esta operação')
        }

        let cliente: ClienteMsResponse
        let conta: ContaMsResponse
        let gerente: GerenteMsResponse

        try {
            [cliente, conta] = await Promise.all([
                httpClient.get<ClienteMsResponse>(
                    `${env.upstreams.cliente}/clientes/${cpf}`,
                    upstreamHeaders),
                httpClient.get<ContaMsResponse>(
                    `${env.upstreams.conta}/contas/cpf/${cpf}`,
                    upstreamHeaders)
            ]);

            gerente = await httpClient.get<GerenteMsResponse>(
                `${env.upstreams.gerente}/gerentes/${conta.gerenteCpf}`,
                upstreamHeaders
            );
        } catch (error) {
            throw error;
        }


        return {
            cpf: cliente.cpf,
            nome: cliente.nome,
            telefone: cliente.telefone,
            email: cliente.email,
            endereco: cliente.endereco,
            cidade: cliente.cidade,
            estado: cliente.estado,
            salario: cliente.salario,
            conta: conta.numeroConta,
            saldo: String(conta.saldo),
            limite: conta.limite,
            gerente: gerente.cpf,
            gerente_nome: gerente.nome,
            gerente_email: gerente.email
        };
    });
}
