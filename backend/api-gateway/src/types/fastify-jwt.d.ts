import '@fastify/jwt';

declare module '@fastify/jwt' {
  interface FastifyJWT {
    payload: {
      sub: string;
      role: 'CLIENTE' | 'GERENTE' | 'ADMIN';
      email: string;
    };
    user: {
      sub: string;
      role: 'CLIENTE' | 'GERENTE' | 'ADMIN';
      email: string;
    };
  }
}
