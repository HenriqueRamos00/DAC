package com.ufpr.bantads.conta.infrastructure.cache.redis.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ufpr.bantads.conta.infrastructure.cache.redis.model.ContaCache;

public interface ContaCacheRedisRepository extends CrudRepository<ContaCache, String> {

    Optional<ContaCache> findByNumeroConta(String numero);

    Optional<ContaCache> findByClienteCpf(String clienteCpf);

    Optional<ContaCache> eventId(String clienteCpf);

}
