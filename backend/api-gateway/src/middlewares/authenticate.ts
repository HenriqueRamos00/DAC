import type { FastifyRequest, FastifyReply } from 'fastify';
import { UnauthorizedError, ForbiddenError } from '../hooks/errors.ts';

export async function authenticate(request: FastifyRequest, _reply: FastifyReply) {
  try {
    await request.jwtVerify();
  } catch {
    throw new UnauthorizedError();
  }
}

export function authorize(...roles: Array<'CLIENTE' | 'GERENTE' | 'ADMIN'>) {
  return async (request: FastifyRequest, reply: FastifyReply) => {
    await authenticate(request, reply);
    if (!roles.includes(request.user.role)) {
      throw new ForbiddenError();
    }
  };
}