import fp from 'fastify-plugin';
import jwt from '@fastify/jwt';
import type { FastifyInstance } from 'fastify';
import { env } from '../config/env.ts';

export default fp(async (gateway: FastifyInstance) => {
  await gateway.register(jwt, {
    secret: env.JWT_SECRET,
    sign: { expiresIn: env.JWT_EXPIRES_IN },
  });
});