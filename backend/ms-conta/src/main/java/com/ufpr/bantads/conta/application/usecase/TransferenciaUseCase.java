package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.application.dto.request.TransferenciaRequest;
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

        Double saldoOrigem = contaOrigem.getSaldo().doubleValue();
        Double limiteOrigem = contaOrigem.getLimite().doubleValue();

        if ((saldoOrigem + limiteOrigem) < valor) {
            throw new IllegalArgumentException("Saldo insuficiente para transferência");
        }

        Double novoSaldoOrigem = saldoOrigem - valor;
        contaOrigem.setSaldo(BigDecimal.valueOf(novoSaldoOrigem));
        contaCommandRepository.save(contaOrigem);

        Double saldoDestino = contaDestino.getSaldo().doubleValue();
        Double novoSaldoDestino = saldoDestino + valor;
        contaDestino.setSaldo(BigDecimal.valueOf(novoSaldoDestino));
        contaCommandRepository.save(contaDestino);

        TransferenciaCommand transferencia = TransferenciaCommand.builder()
                .contaOrigemId(contaOrigem.getId())
                .contaDestinoId(contaDestino.getId())
                .valor(BigDecimal.valueOf(valor))
                .build();

        transferenciaCommandRepository.save(transferencia);

        movimentacaoEventPublisher.publish(MovimentacaoEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .movimentacaoId(transferencia.getId())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .valor(BigDecimal.valueOf(valor))
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
