import type { FastifyRequest } from 'fastify';

export function buildUpstreamHeaders(
  request: FastifyRequest,
): Record<string, string> {
  const headers: Record<string, string> = {
    'x-request-id': request.id,
    'x-forwarded-host': request.hostname,
  };

  if (request.user) {
    headers['x-user-id'] = String(request.user.sub);
    headers['x-user-role'] = request.user.role;
    headers['x-user-email'] = request.user.email;
  }

  return headers;
}
