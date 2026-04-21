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
        
            default:
                break;
        }
        
    }

    private void sincronizarDeposito(MovimentacaoEvent event) {
        ContaQuery conta = contaQueryRepository.findByNumeroConta(event.getNumeroContaDestino())
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        conta.setSaldo(event.getNovoSaldoContaDestino());
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
