package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.application.dto.response.TransferenciaResponse;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.model.entity.TransferenciaCommand;
import com.ufpr.bantads.conta.domain.model.enums.TipoMovimentacao;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.TransferenciaCommandRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.MovimentacaoEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferenciaUseCase {

    private final ContaCommandRepository contaCommandRepository;

    private final TransferenciaCommandRepository transferenciaCommandRepository;

    private final MovimentacaoEventPublisher movimentacaoEventPublisher;

    @Transactional
    public TransferenciaResponse execute(String numeroContaOrigem, String numeroContaDestino, Double valor) {

        if (numeroContaOrigem.equals(numeroContaDestino)) {
             throw new IllegalArgumentException("Não é possível transferir para a mesma conta");
        }

        ContaCommand contaOrigem = contaCommandRepository.findByNumeroConta(numeroContaOrigem)
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));

        ContaCommand contaDestino = contaCommandRepository.findByNumeroConta(numeroContaDestino)
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));

        BigDecimal valorTransferencia = BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);

        if (contaOrigem.getSaldo().add(contaOrigem.getLimite()).compareTo(valorTransferencia) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para transferência");
        }

        BigDecimal novoSaldoOrigem =
            contaOrigem.getSaldo().subtract(valorTransferencia).setScale(2, RoundingMode.HALF_UP);
        contaOrigem.setSaldo(novoSaldoOrigem);
        contaCommandRepository.save(contaOrigem);

        BigDecimal novoSaldoDestino =
            contaDestino.getSaldo().add(valorTransferencia).setScale(2, RoundingMode.HALF_UP);
        contaDestino.setSaldo(novoSaldoDestino);
        contaCommandRepository.save(contaDestino);

        TransferenciaCommand transferencia = TransferenciaCommand.builder()
                .contaOrigemId(contaOrigem.getId())
                .contaDestinoId(contaDestino.getId())
                .valor(valorTransferencia)
                .build();

        transferenciaCommandRepository.save(transferencia);

        movimentacaoEventPublisher.publish(MovimentacaoEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .movimentacaoId(transferencia.getId())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .valor(valorTransferencia)
                .dataHora(transferencia.getDataHora())
                .numeroContaOrigem(contaOrigem.getNumeroConta())
                .numeroContaDestino(contaDestino.getNumeroConta())
                .novoSaldoContaOrigem(contaOrigem.getSaldo())
                .novoSaldoContaDestino(contaDestino.getSaldo())
                .build());
        
        return new TransferenciaResponse(
            contaOrigem.getNumeroConta(), 
            contaOrigem.getSaldo().doubleValue(), 
            transferencia.getDataHora().toString());
    }
}
