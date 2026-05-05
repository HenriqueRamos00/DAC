export const env = {
  PORT: Number(process.env.GATEWAY_PORT) || 3000,
  JWT_SECRET: process.env.JWT_SECRET || 'dev-secret-change-me',
  JWT_EXPIRES_IN: process.env.JWT_EXPIRES_IN || '1h',
  upstreams: {
    auth:    process.env.AUTH_URL    || 'http://localhost:8081',
    cliente: process.env.CLIENTE_URL || 'http://localhost:8082',
    conta:   process.env.CONTA_URL   || 'http://localhost:8083',
    gerente: process.env.GERENTE_URL || 'http://localhost:8084',
    admin:   process.env.ADMIN_URL   || 'http://localhost:8085',
    sagas:   process.env.SAGAS_URL   || 'http://localhost:8086',
  },
};