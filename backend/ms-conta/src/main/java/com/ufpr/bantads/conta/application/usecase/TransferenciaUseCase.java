package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.application.dto.request.TransferenciaRequest;
import com.ufpr.bantads.conta.application.dto.response.TransferenciaResponse;
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
public class TransferenciaUseCase {

    public final ContaCommandRepository contaCommandRepository;

    public final MovimentacaoCommandRepository movimentacaoCommandRepository;

    public final MovimentacaoEventPublisher movimentacaoEventPublisher;

    @Transactional
    public TransferenciaResponse execute(TransferenciaRequest req) {

        if (req.numeroContaOrigem().equals(req.numeroContaDestino())) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta");
        }

        ContaCommand contaOrigem = contaCommandRepository.findByNumeroConta(req.numeroContaOrigem())
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));

        ContaCommand contaDestino = contaCommandRepository.findByNumeroConta(req.numeroContaDestino())
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));

        Double saldoOrigem = contaOrigem.getSaldo().doubleValue();
        Double limiteOrigem = contaOrigem.getLimite().doubleValue();

        if ((saldoOrigem + limiteOrigem) < req.valor()) {
            throw new IllegalArgumentException("Saldo insuficiente para transferência");
        }

        Double novoSaldoOrigem = saldoOrigem - req.valor();
        contaOrigem.setSaldo(BigDecimal.valueOf(novoSaldoOrigem));
        contaCommandRepository.save(contaOrigem);

        Double saldoDestino = contaDestino.getSaldo().doubleValue();
        Double novoSaldoDestino = saldoDestino + req.valor();
        contaDestino.setSaldo(BigDecimal.valueOf(novoSaldoDestino));
        contaCommandRepository.save(contaDestino);

        MovimentacaoCommand movOrigem = MovimentacaoCommand.builder()
                .contaId(contaOrigem.getId())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .valor(BigDecimal.valueOf(req.valor()))
                .build();

        movimentacaoCommandRepository.save(movOrigem);

        MovimentacaoCommand movDestino = MovimentacaoCommand.builder()
                .contaId(contaDestino.getId())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .valor(BigDecimal.valueOf(req.valor()))
                .build();

        movimentacaoCommandRepository.save(movDestino);

        movimentacaoEventPublisher.publish(MovimentacaoEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .movimentacaoId(movOrigem.getId())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .valor(BigDecimal.valueOf(req.valor()))
                .dataHora(movOrigem.getDataHora())
                .numeroContaOrigem(contaOrigem.getNumeroConta())
                .numeroContaDestino(contaDestino.getNumeroConta())
                .novoSaldoContaOrigem(contaOrigem.getSaldo())
                .novoSaldoContaDestino(contaDestino.getSaldo())
                .build());
        
        return new TransferenciaResponse(
            contaOrigem.getNumeroConta(), 
            contaOrigem.getSaldo().doubleValue(), 
            movOrigem.getDataHora().toString());
    }
}
