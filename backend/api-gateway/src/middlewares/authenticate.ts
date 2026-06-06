import type { FastifyRequest, FastifyReply } from 'fastify';
import { UnauthorizedError, ForbiddenError } from '../hooks/errors.ts';
import { tokenBlacklist } from '../services/token-blacklist.ts';

export async function authenticate(request: FastifyRequest, _reply: FastifyReply) {
  try {
    await request.jwtVerify();
  } catch {
    throw new UnauthorizedError('Token inválido ou ausente');
  }

  const revoked = await tokenBlacklist.isRevoked(request.user);

  if (revoked) {
    throw new UnauthorizedError('Token inválido');
  }
}

export function authorize(...roles: Array<'CLIENTE' | 'GERENTE' | 'ADMINISTRADOR'>) {
  return async (request: FastifyRequest, reply: FastifyReply) => {
    await authenticate(request, reply);
    if (!roles.includes(request.user.role)) {
      throw new ForbiddenError();
    }
  };
}

export async function revokeToken(request: FastifyRequest): Promise<void> {
  await tokenBlacklist.revoke(request.user);
}
