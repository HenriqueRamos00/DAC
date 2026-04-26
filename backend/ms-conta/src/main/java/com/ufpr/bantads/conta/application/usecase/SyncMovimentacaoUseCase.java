package com.ufpr.bantads.conta.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;
import com.ufpr.bantads.conta.domain.model.entity.MovimentacaoQuery;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.domain.repository.MovimentacaoQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SyncMovimentacaoUseCase {

    private final MovimentacaoQueryRepository movimentacaoQueryRepository;

    private final ContaQueryRepository contaQueryRepository;

    @Transactional
    public void execute(MovimentacaoEvent event) {

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new IllegalArgumentException("Evento de movimentação sem eventId");
        }

        if (movimentacaoQueryRepository.existsByEventId(event.getEventId())) {
            return;
        }

        switch (event.getTipo()) {
            case DEPOSITO:
                sincronizarDeposito(event);
                break;
            case SAQUE:
                sincronizarSaque(event);
                break;
            case TRANSFERENCIA:
                sincronoizarTransferencia(event);
                break;
            default:
                break;
        }
        
    }

    private void sincronoizarTransferencia(MovimentacaoEvent event) {
        ContaQuery contaOrigem = contaQueryRepository.findByNumeroConta(event.getNumeroContaOrigem())
            .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));
        contaOrigem.setSaldo(event.getNovoSaldoContaOrigem());
        contaQueryRepository.save(contaOrigem);

        ContaQuery contaDestino = contaQueryRepository.findByNumeroConta(event.getNumeroContaDestino())
            .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));
        contaDestino.setSaldo(event.getNovoSaldoContaDestino());
        contaQueryRepository.save(contaDestino);

        MovimentacaoQuery movimentacao = MovimentacaoQuery.builder()
            .eventId(event.getEventId())
            .contaOrigemNumero(contaOrigem.getNumeroConta())
            .clienteOrigemNome(contaOrigem.getClienteNome())
            .contaDestinoNumero(contaDestino.getNumeroConta())
            .clienteDestinoNome(contaDestino.getClienteNome())
            .tipo(event.getTipo())
            .valor(event.getValor())
            .dataHora(event.getDataHora())
            .build();
        movimentacaoQueryRepository.save(movimentacao);
    }

    private void sincronizarSaque(MovimentacaoEvent event) {
        ContaQuery conta = contaQueryRepository.findByNumeroConta(event.getNumeroContaOrigem())
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        conta.setSaldo(event.getNovoSaldoContaOrigem());
        contaQueryRepository.save(conta);

        MovimentacaoQuery movimentacao = MovimentacaoQuery.builder()
            .eventId(event.getEventId())
            .contaOrigemNumero(conta.getNumeroConta())
            .clienteOrigemNome(conta.getClienteNome())
            .contaDestinoNumero(null)
            .clienteDestinoNome(null)
            .tipo(event.getTipo())
            .valor(event.getValor())
            .dataHora(event.getDataHora())
            .build();
        movimentacaoQueryRepository.save(movimentacao);
    }

    private void sincronizarDeposito(MovimentacaoEvent event) {
        ContaQuery conta = contaQueryRepository.findByNumeroConta(event.getNumeroContaOrigem())
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        conta.setSaldo(event.getNovoSaldoContaOrigem());
        contaQueryRepository.save(conta);

        MovimentacaoQuery movimentacao = MovimentacaoQuery.builder()
            .eventId(event.getEventId())
            .contaOrigemNumero(conta.getNumeroConta())
            .clienteOrigemNome(conta.getClienteNome())
            .contaDestinoNumero(null)
            .clienteDestinoNome(null)
            .tipo(event.getTipo())
            .valor(event.getValor())
            .dataHora(event.getDataHora())
            .build();
        movimentacaoQueryRepository.save(movimentacao);
    }

}
