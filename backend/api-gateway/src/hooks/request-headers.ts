import type { FastifyRequest, RequestGenericInterface, RawServerBase } from 'fastify';
import type { IncomingHttpHeaders } from 'node:http';

export function injectRequestId(
  request: FastifyRequest<RequestGenericInterface, RawServerBase>,
  headers: IncomingHttpHeaders,
): IncomingHttpHeaders {
  const out: IncomingHttpHeaders = {
    ...headers,
    'x-request-id': request.id,
    'x-forwarded-host': request.hostname,
  };

  if (request.user) {
    out['x-user-id']    = request.user.sub;
    out['x-user-role']  = request.user.role;
    out['x-user-email'] = request.user.email;
  }
  return out;
}
