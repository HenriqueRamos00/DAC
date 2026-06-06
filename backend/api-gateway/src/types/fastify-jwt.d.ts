import '@fastify/jwt';

declare module '@fastify/jwt' {
  interface FastifyJWT {
    payload: {
      sub: string;
      role: 'CLIENTE' | 'GERENTE' | 'ADMINISTRADOR';
      email: string;
      jti: string;
    };
    user: {
      sub: string;
      role: 'CLIENTE' | 'GERENTE' | 'ADMINISTRADOR';
      email: string;
      jti: string;
      iat: number;
      exp: number;
    };
  }
}
