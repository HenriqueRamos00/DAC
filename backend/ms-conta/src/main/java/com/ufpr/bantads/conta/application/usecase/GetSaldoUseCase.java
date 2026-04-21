package com.ufpr.bantads.conta.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.conta.application.dto.response.SaldoResponse;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetSaldoUseCase {

    private final ContaQueryRepository contaQueryRepository;

    public SaldoResponse execute(String conta) {
        return contaQueryRepository.findByNumeroConta(conta)
            .map(SaldoResponse::fromEntity)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }

}
