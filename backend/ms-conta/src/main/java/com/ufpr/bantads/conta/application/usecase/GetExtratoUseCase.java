package com.ufpr.bantads.conta.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.conta.application.dto.response.Extrato;
import com.ufpr.bantads.conta.application.dto.response.ExtratoResponse;
import com.ufpr.bantads.conta.domain.exception.ContaNaoEncontradaException;
import com.ufpr.bantads.conta.domain.exception.RequisicaoInvalidaException;
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
        if (numeroConta == null || numeroConta.isBlank()) {
            throw new RequisicaoInvalidaException("Número da conta é obrigatório");
        }

        ContaQuery conta = contaQueryRepository.findByNumeroConta(numeroConta)
            .orElseThrow(ContaNaoEncontradaException::new);

        List<Extrato> movimentacoes = movimentacaoQueryRepository
            .findByContaOrigemNumeroOrContaDestinoNumeroOrderByDataHoraAsc(numeroConta, numeroConta)
            .stream()
            .map(Extrato::fromEntity)
            .toList();

        return new ExtratoResponse(
            conta.getNumeroConta(), 
            conta.getSaldo().doubleValue(), 
            movimentacoes);
    }
}
