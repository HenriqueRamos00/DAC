package com.ufpr.bantads.conta.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.conta.application.dto.response.Extrato;
import com.ufpr.bantads.conta.application.dto.response.ExtratoResponse;
import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.domain.repository.MovimentacaoQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetExtratoUseCase {
    private final ContaQueryRepository contaQueryRepository;
    private final MovimentacaoQueryRepository movimentacaoQueryRepository;

    public ExtratoResponse getExtrato(String numeroConta) {
        ContaQuery conta = contaQueryRepository.findByNumeroConta(numeroConta)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        List<Extrato> movimentacoes = movimentacaoQueryRepository
            .findByContaOrigemNumeroOrContaDestinoNumero(numeroConta, numeroConta)
            .stream()
            .map(Extrato::fromEntity)
            .toList();

        return new ExtratoResponse(
            conta.getNumeroConta(), 
            conta.getSaldo().doubleValue(), 
            movimentacoes);
    }
}
