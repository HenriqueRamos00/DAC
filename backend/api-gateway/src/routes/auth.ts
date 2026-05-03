import type { FastifyInstance } from 'fastify';
import { httpClient, UpstreamError } from '../services/http-client.ts';
import { env } from '../config/env.ts';
import { UnauthorizedError } from '../hooks/errors.ts';
import type { LoginMsResponseDto, LoginRequestDto, LoginResponseDto } from '../types/dto/login.ts';

export async function registerAuthRoutes(gateway: FastifyInstance) {
  
  gateway.post<{ 
    Body: LoginRequestDto,
    Reply: LoginResponseDto
  }>('/login', async (request, reply) => {

    const { login, senha } = request.body;

    let auth: LoginMsResponseDto;
    try {
      auth = await httpClient.post<LoginMsResponseDto>(
        `${env.upstreams.auth}/login`,
        { login, senha },
        { 'x-request-id': request.id },
      );
    } catch (err) {
      if (err instanceof UpstreamError && (err.statusCode === 401 || err.statusCode === 404)) {
        throw new UnauthorizedError('Credenciais inválidas.');
      }
      throw err; // outros erros viram 5xx pelo handler global
    }

    console.log(auth)

  const accessToken = await reply.jwtSign({
        sub: auth.cpf,
        role: auth.tipoUsuario,
        email: auth.email,
      });

  return {
      access_token: accessToken,
      token_type: "bearer",
      tipo: auth.tipoUsuario,
      usuario: {
        nome: auth.nome,
        cpf: auth.cpf,
        email: auth.email,
      },
    };
  });
}