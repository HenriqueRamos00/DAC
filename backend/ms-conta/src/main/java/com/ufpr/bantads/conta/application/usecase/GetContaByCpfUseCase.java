package com.ufpr.bantads.conta.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.conta.application.dto.response.ContaResponse;
import com.ufpr.bantads.conta.domain.exception.ContaNaoEncontradaException;
import com.ufpr.bantads.conta.domain.exception.RequisicaoInvalidaException;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.infrastructure.cache.redis.model.ContaCache;
import com.ufpr.bantads.conta.infrastructure.cache.redis.repository.ContaCacheRedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetContaByCpfUseCase {

    private final ContaQueryRepository contaQueryRepository;

    private final ContaCacheRedisRepository cacheRedisRepository;

    public ContaResponse execute(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new RequisicaoInvalidaException("CPF do cliente é obrigatório");
        }

        return contaQueryRepository.findByClienteCpf(cpf)
            .map(conta -> {
                ContaCache cache = cacheRedisRepository
                    .findById(conta.getNumeroConta())
                    .orElse(null);

                if (cache != null) {
                    return ContaResponse.fromEntity(conta, cache.getSaldo().doubleValue());
                }

                return ContaResponse.fromEntity(conta);
            })
            .orElseThrow(ContaNaoEncontradaException::new);
    }

}
