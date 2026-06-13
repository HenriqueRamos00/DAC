package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.application.dto.response.TransferenciaResponse;
import com.ufpr.bantads.conta.domain.exception.ContaNaoEncontradaException;
import com.ufpr.bantads.conta.domain.exception.RegraNegocioException;
import com.ufpr.bantads.conta.domain.exception.RequisicaoInvalidaException;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.model.entity.TransferenciaCommand;
import com.ufpr.bantads.conta.domain.model.enums.TipoMovimentacao;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.TransferenciaCommandRepository;
import com.ufpr.bantads.conta.infrastructure.cache.redis.model.ContaCache;
import com.ufpr.bantads.conta.infrastructure.cache.redis.repository.ContaCacheRedisRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.MovimentacaoEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferenciaUseCase {

    private final ContaCommandRepository contaCommandRepository;

    private final TransferenciaCommandRepository transferenciaCommandRepository;

    private final ContaCacheRedisRepository cacheRedisRepository;

    private final MovimentacaoEventPublisher movimentacaoEventPublisher;

    @Transactional
    public TransferenciaResponse execute(String numeroContaOrigem, String numeroContaDestino, Double valor) {
        validar(numeroContaOrigem, numeroContaDestino, valor);

        if (numeroContaOrigem.equals(numeroContaDestino)) {
             throw new RegraNegocioException("Não é possível transferir para a mesma conta");
        }

        ContaCommand contaOrigem = contaCommandRepository.findByNumeroConta(numeroContaOrigem)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta de origem não encontrada"));

        ContaCommand contaDestino = contaCommandRepository.findByNumeroConta(numeroContaDestino)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta de destino não encontrada"));

        BigDecimal valorTransferencia = BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);

        if (contaOrigem.getSaldo().add(contaOrigem.getLimite()).compareTo(valorTransferencia) < 0) {
            throw new RegraNegocioException("Saldo insuficiente para transferência");
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

        String eventId = UUID.randomUUID().toString();

        movimentacaoEventPublisher.publish(MovimentacaoEvent.builder()
                .eventId(eventId)
                .movimentacaoId(transferencia.getId())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .valor(valorTransferencia)
                .dataHora(transferencia.getDataHora())
                .numeroContaOrigem(contaOrigem.getNumeroConta())
                .numeroContaDestino(contaDestino.getNumeroConta())
                .novoSaldoContaOrigem(contaOrigem.getSaldo())
                .novoSaldoContaDestino(contaDestino.getSaldo())
                .build());

        cacheRedisRepository.save(ContaCache.builder()
                .clienteCpf(contaOrigem.getClienteCpf())
                .numeroConta(contaOrigem.getNumeroConta())
                .eventId(eventId)
                .saldo(novoSaldoOrigem)
                .build());

        cacheRedisRepository.save(ContaCache.builder()
                .clienteCpf(contaDestino.getClienteCpf())
                .numeroConta(contaDestino.getNumeroConta())
                .eventId(eventId)
                .saldo(novoSaldoDestino)
                .build());
        
        return new TransferenciaResponse(
            contaOrigem.getNumeroConta(), 
            contaDestino.getNumeroConta(),
            valorTransferencia.doubleValue(),
            contaOrigem.getSaldo().doubleValue(), 
            transferencia.getDataHora().toString());
    }

    private void validar(String numeroContaOrigem, String numeroContaDestino, Double valor) {
        if (numeroContaOrigem == null || numeroContaOrigem.isBlank()) {
            throw new RequisicaoInvalidaException("Conta de origem é obrigatória");
        }

        if (numeroContaDestino == null || numeroContaDestino.isBlank()) {
            throw new RequisicaoInvalidaException("Conta de destino é obrigatória");
        }

        if (valor == null) {
            throw new RequisicaoInvalidaException("Valor da transferência é obrigatório");
        }

        if (valor <= 0) {
            throw new RequisicaoInvalidaException("Valor deve ser maior que zero");
        }
    }
}
