package com.ufpr.bantads.conta.infrastructure.cache.redis.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.infrastructure.cache.redis.model.ContaCache;
import com.ufpr.bantads.conta.infrastructure.cache.redis.repository.ContaCacheRedisRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SyncListener {

    private final ContaCacheRedisRepository cacheRedisRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictOldCache(MovimentacaoEvent event) {
        evictIfPresent(event.getNumeroContaOrigem());
        evictIfPresent(event.getNumeroContaDestino());
    }

    private void evictIfPresent(String numeroConta) {
        if (numeroConta == null || numeroConta.isBlank()) {
            return;
        }

        cacheRedisRepository.deleteById(ContaCache.idForNumeroConta(numeroConta));
    }

}
