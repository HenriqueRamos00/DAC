import '@fastify/jwt';

declare module '@fastify/jwt' {
  interface FastifyJWT {
    payload: {
      sub: string;
      role: 'CLIENTE' | 'GERENTE' | 'ADMINISTRADOR';
      email: string;
    };
    user: {
      sub: string;
      role: 'CLIENTE' | 'GERENTE' | 'ADMINISTRADOR';
      email: string;
    };
  }
}
