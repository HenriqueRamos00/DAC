package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.application.dto.response.DepositoSaqueResponse;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.model.entity.SaqueCommand;
import com.ufpr.bantads.conta.domain.model.enums.TipoMovimentacao;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.SaqueCommandRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.MovimentacaoEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SacarUseCase {

    private final ContaCommandRepository contaCommandRepository;

    private final SaqueCommandRepository saqueCommandRepository;

    private final MovimentacaoEventPublisher movimentacaoEventPublisher;

    @Transactional
    public DepositoSaqueResponse execute(String numeroConta, Double valor) {
        ContaCommand conta = contaCommandRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        BigDecimal valorSaque = BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);

        if (conta.getSaldo().add(conta.getLimite()).compareTo(valorSaque) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente considerando o limite");
        }


        BigDecimal novoSaldo = conta.getSaldo().subtract(valorSaque).setScale(2, RoundingMode.HALF_UP);
        conta.setSaldo(novoSaldo);
        contaCommandRepository.save(conta);

        SaqueCommand saque = SaqueCommand.builder()
                .contaId(conta.getId())
                .valor(valorSaque)
                .build();

        saqueCommandRepository.save(saque);

        movimentacaoEventPublisher.publish(MovimentacaoEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .movimentacaoId(saque.getId())
                .tipo(TipoMovimentacao.SAQUE)
                .valor(saque.getValor())
                .dataHora(saque.getDataHora())
                .numeroContaOrigem(conta.getNumeroConta())
                .novoSaldoContaOrigem(conta.getSaldo())
                .build());

        return new DepositoSaqueResponse(
                conta.getNumeroConta(),
                conta.getSaldo().doubleValue(),
                saque.getDataHora().toString());
    }
}
