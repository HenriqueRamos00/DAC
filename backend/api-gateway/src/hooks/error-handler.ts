import type { FastifyError, FastifyInstance } from 'fastify';
import { GatewayError, ServiceUnavailableError, UpstreamError } from './errors.ts';

function normalizeUpstreamErrorBody(error: UpstreamError) {
  if (error.body && typeof error.body === 'object' && !Array.isArray(error.body)) {
    return {
      ...error.body,
      statusCode: error.statusCode,
      message: error.message,
    };
  }

  return {
    statusCode: error.statusCode,
    error: error.name,
    message: error.message,
  };
}

export function registerErrorHandler(gateway: FastifyInstance) {
  gateway.setErrorHandler<FastifyError | GatewayError>((error, request, reply) => {
    request.log.error(error);

    if (error instanceof UpstreamError) {
      return reply.code(error.statusCode).send(normalizeUpstreamErrorBody(error));
    }

    if (error instanceof GatewayError) {
      return reply.code(error.statusCode).send({
        statusCode: error.statusCode,
        error: error.name,
        message: error.message,
      });
    }

    if (error.code === 'ECONNREFUSED') {
      const serviceError = new ServiceUnavailableError('upstream');
      return reply.code(serviceError.statusCode).send({
        statusCode: serviceError.statusCode,
        error: serviceError.name,
        message: serviceError.message,
      });
    }

    reply.code(error.statusCode || 500).send({
      statusCode: error.statusCode || 500,
      error: error.name || 'InternalServerError',
      message: error.message,
    });
  });
}
