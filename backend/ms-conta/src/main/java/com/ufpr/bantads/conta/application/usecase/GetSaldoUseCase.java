package com.ufpr.bantads.conta.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.conta.application.dto.response.SaldoResponse;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.infrastructure.cache.redis.model.ContaCache;
import com.ufpr.bantads.conta.infrastructure.cache.redis.repository.ContaCacheRedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetSaldoUseCase {

    private final ContaQueryRepository contaQueryRepository;

    private final ContaCacheRedisRepository cacheRedisRepository;

    public SaldoResponse execute(String conta) {
        ContaCache cache = cacheRedisRepository.findById(ContaCache.idForNumeroConta(conta)).orElse(null);

        if (cache != null) {
            return new SaldoResponse(
                cache.getClienteCpf(),
                cache.getNumeroConta(),
                cache.getSaldo().doubleValue()
            );
        }

        return contaQueryRepository.findByNumeroConta(conta)
            .map(SaldoResponse::fromEntity)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }

}
