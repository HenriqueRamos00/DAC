package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.application.dto.response.DepositoSaqueResponse;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.model.entity.MovimentacaoCommand;
import com.ufpr.bantads.conta.domain.model.enums.TipoMovimentacao;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.MovimentacaoCommandRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.MovimentacaoEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SacarUseCase {

    public final ContaCommandRepository contaCommandRepository;

    public final MovimentacaoCommandRepository movimentacaoCommandRepository;

    public final MovimentacaoEventPublisher movimentacaoEventPublisher;

    @Transactional
    public DepositoSaqueResponse execute(String numeroConta, Double valor) {
        ContaCommand conta = contaCommandRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        Double saldoAtual = conta.getSaldo().doubleValue();
        Double limite = conta.getLimite().doubleValue();

        if ((saldoAtual + limite) < valor) {
            throw new IllegalArgumentException("Saldo insuficiente considerando o limite");
        }


        Double novoSaldo = saldoAtual - valor;
        conta.setSaldo(BigDecimal.valueOf(novoSaldo));
        contaCommandRepository.save(conta);

        MovimentacaoCommand movimentacao = MovimentacaoCommand.builder()
                .contaId(conta.getId())
                .tipo(TipoMovimentacao.SAQUE)
                .valor(BigDecimal.valueOf(valor))
                .build();

        movimentacaoCommandRepository.save(movimentacao);

        movimentacaoEventPublisher.publish(MovimentacaoEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .movimentacaoId(movimentacao.getId())
                .tipo(movimentacao.getTipo())
                .valor(movimentacao.getValor())
                .dataHora(movimentacao.getDataHora())
                .numeroContaDestino(conta.getNumeroConta())
                .novoSaldoContaDestino(conta.getSaldo())
                .build());

        return new DepositoSaqueResponse(
                conta.getNumeroConta(),
                conta.getSaldo().doubleValue(),
                movimentacao.getDataHora().toString());
    }
}
