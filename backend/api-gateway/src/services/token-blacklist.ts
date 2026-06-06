import { Redis } from "ioredis";
import { env } from "../config/env.ts";
import { ServiceUnavailableError, UnauthorizedError } from "../hooks/errors.ts";

type JwtUser = {
    jti?: string;
    exp?: number;
}

const redis = new Redis(env.REDIS_URL);

redis.on('connect', () => {
  console.log('[Redis] Conectado');
});

redis.on('error', (error) => {
  console.error('[Redis] Erro na conexão', error);
});

function nowInSeconds() : number {
    return Math.floor(Date.now() / 1000);
}

function buildBlacklistKey(user: JwtUser): string {
  if (!user.jti) {
    throw new UnauthorizedError('Token sem identificador');
  }

  return `bantads:gateway:jwt:blacklist:jti:${user.jti}`;
}

function calculateTtlSeconds(user: JwtUser): number {
  if (!user.exp) {
    return 60 * 60;
  }

  const ttl = user.exp - nowInSeconds();

  if (ttl <= 0) {
    throw new UnauthorizedError('Token expirado');
  }

  return ttl;
}

function isRedisConnectionError(error: unknown): boolean {
  return error instanceof Error && (
    error.message.includes('Connection') ||
    error.message.includes('ECONNREFUSED') ||
    error.message.includes('maxRetriesPerRequest') ||
    error.message.includes('enableOfflineQueue')
  );
}

export const tokenBlacklist = {
  async revoke(user: JwtUser): Promise<void> {
    const key = buildBlacklistKey(user);
    const ttlSeconds = calculateTtlSeconds(user);

    try {
      await redis.set(key, 'revoked', 'EX', ttlSeconds);
    } catch (error) {
      console.error('[Redis] Falha ao revogar token', {
        key,
        ttlSeconds,
        error,
      });

      if (isRedisConnectionError(error)) {
        throw new ServiceUnavailableError('Não foi possível acessar o serviço de blacklist');
      }

      throw error;
    }
  },

  async isRevoked(user: JwtUser): Promise<boolean> {
    const key = buildBlacklistKey(user);

    try {
      const value = await redis.get(key);
      return value === 'revoked';
    } catch (error) {
      console.error('[Redis] Falha ao consultar blacklist', {
        key,
        error,
      });

      if (isRedisConnectionError(error)) {
        throw new ServiceUnavailableError('Não foi possível verificar a validade do token');
      }

      throw error;
    }
  },
};