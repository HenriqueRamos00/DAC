import type { FastifyRequest, RequestGenericInterface, RawServerBase } from 'fastify';
import type { IncomingHttpHeaders } from 'node:http';

export function injectRequestId(
  request: FastifyRequest<RequestGenericInterface, RawServerBase>,
  headers: IncomingHttpHeaders,
): IncomingHttpHeaders {
  return {
    ...headers,
    'x-request-id': request.id,
    'x-forwarded-host': request.hostname,
  };
}
