package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.application.dto.response.DepositoSaqueResponse;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.model.entity.DepositoCommand;
import com.ufpr.bantads.conta.domain.model.enums.TipoMovimentacao;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.DepositoCommandRepository;
import com.ufpr.bantads.conta.infrastructure.cache.redis.model.ContaCache;
import com.ufpr.bantads.conta.infrastructure.cache.redis.repository.ContaCacheRedisRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.MovimentacaoEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositarUseCase {

    private final ContaCommandRepository contaCommandRepository;

    private final DepositoCommandRepository depositoCommandRepository;

    private final ContaCacheRedisRepository cacheRedisRepository;

    private final MovimentacaoEventPublisher movimentacaoEventPublisher;

    @Transactional
    public DepositoSaqueResponse execute(String numeroConta, Double valor) {
        ContaCommand conta = contaCommandRepository.findByNumeroConta(numeroConta)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        BigDecimal valorDeposito = BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);
        BigDecimal novoSaldo = conta.getSaldo().add(valorDeposito).setScale(2, RoundingMode.HALF_UP);

        conta.setSaldo(novoSaldo);
        contaCommandRepository.save(conta);

        DepositoCommand deposito = DepositoCommand.builder()
            .contaId(conta.getId())
            .valor(valorDeposito)
            .build();

        depositoCommandRepository.save(deposito);

        String eventId = UUID.randomUUID().toString();

        movimentacaoEventPublisher.publish(MovimentacaoEvent.builder()
            .eventId(eventId)
            .movimentacaoId(deposito.getId())
            .tipo(TipoMovimentacao.DEPOSITO)
            .valor(deposito.getValor())
            .dataHora(deposito.getDataHora())
            .numeroContaOrigem(conta.getNumeroConta())
            .novoSaldoContaOrigem(conta.getSaldo())
            .build());

        cacheRedisRepository.save(ContaCache.builder()
            .clienteCpf(conta.getClienteCpf())
            .numeroConta(conta.getNumeroConta())
            .saldo(novoSaldo)
            .eventId(eventId)
            .build());

        return new DepositoSaqueResponse(
            conta.getNumeroConta(), 
            conta.getSaldo().doubleValue(), 
            deposito.getDataHora().toString());
    }
}
