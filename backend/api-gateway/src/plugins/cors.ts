import type { FastifyInstance } from 'fastify';
import cors from '@fastify/cors';

export async function registerCors(gateway: FastifyInstance) {
  await gateway.register(cors, {
    origin: [
      'http://localhost:5173',
      'http://localhost:4173',
    ],
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    credentials: true,
  });
}
