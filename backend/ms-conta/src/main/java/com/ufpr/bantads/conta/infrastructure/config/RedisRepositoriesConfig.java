package com.ufpr.bantads.conta.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(
    basePackages = "com.ufpr.bantads.conta.infrastructure.cache.redis.repository"
)
public class RedisRepositoriesConfig {
}
