export class GatewayError extends Error {
  statusCode: number;

  constructor(statusCode: number, message: string) {
    super(message);
    this.name = 'GatewayError';
    this.statusCode = statusCode;
  }
}

export class BadGatewatError extends GatewayError {
  constructor(service: string) {
    super(502, `Serviço "${service}" indisponível. Tente novamente mais tarde.`);
    this.name = 'BadGatewayError';
  }
}

export class UnauthorizedError extends GatewayError {
  constructor(message = 'O usuário não está logado') {
    super(401, message);
    this.name = 'UnauthorizedError';
  }
}

export class ForbiddenError extends GatewayError {
  constructor(message = 'O usuário não tem permissão para efetuar esta operação') {
    super(403, message);
    this.name = 'ForbiddenError';
  }
}

export class BadRequestError extends GatewayError {
  constructor(message = 'Requisição inválida.') {
    super(400, message);
    this.name = 'BadRequestError';
  }
}

export class NotFoundError extends GatewayError {
  constructor(resource = 'Recurso') {
    super(404, `${resource} não encontrado.`);
    this.name = 'NotFoundError';
  }
}

export class TimeoutError extends GatewayError {
  constructor(service: string) {
    super(504, `Tempo limite excedido ao conectar com "${service}".`);
    this.name = 'TimeoutError';
  }
}

export class ServiceUnavailableError extends GatewayError {
  constructor(message?: string) {
    super(503, `Falha de processamento da requisição: ${message}`);
    this.name = 'ServiceUnavailableError'
  }
}

function extractUpstreamMessage(body: unknown): string | undefined {
  if (typeof body === 'string') {
    const message = body.trim();
    return message.length > 0 ? message : undefined;
  }

  if (body && typeof body === 'object' && 'message' in body) {
    const message = (body as { message?: unknown }).message;
    return typeof message === 'string' && message.trim().length > 0
      ? message
      : undefined;
  }

  return undefined;
}

export class UpstreamError extends GatewayError {
  public body: unknown;
  public url: string;

  constructor(status: number, body: unknown, url: string) {
    super(status, extractUpstreamMessage(body) ?? `Upstream ${url} respondeu ${status}`);
    this.body = body;
    this.url = url;
    this.name = 'UpstreamError';
  }
}
