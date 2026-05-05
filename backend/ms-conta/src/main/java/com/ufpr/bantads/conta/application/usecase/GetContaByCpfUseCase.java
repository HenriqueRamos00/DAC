package com.ufpr.bantads.conta.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.conta.application.dto.response.ContaResponse;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetContaByCpfUseCase {

    private final ContaQueryRepository contaQueryRepository;

    public ContaResponse execute(String cpf) {
        return contaQueryRepository.findByClienteCpf(cpf)
            .map(ContaResponse::fromEntity)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }

}
