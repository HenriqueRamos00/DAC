package com.ufpr.bantads.conta.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import com.ufpr.bantads.conta.application.dto.event.ContaCriadaEvent;
import com.ufpr.bantads.conta.application.dto.event.ContaExcluidaEvent;
import com.ufpr.bantads.conta.application.dto.event.ContaLimiteAlteradoEvent;
import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.domain.exception.ContaNaoEncontradaException;
import com.ufpr.bantads.conta.domain.exception.RequisicaoInvalidaException;
import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;
import com.ufpr.bantads.conta.domain.model.entity.MovimentacaoQuery;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.domain.repository.MovimentacaoQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SyncMovimentacaoUseCase {

    private final MovimentacaoQueryRepository movimentacaoQueryRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final ContaQueryRepository contaQueryRepository;

    @Transactional
    public void sincronizarContaCriada(ContaCriadaEvent event) {
        if (contaQueryRepository.findByNumeroConta(event.getNumeroConta()).isPresent()) {
            return;
        }

        ContaQuery conta = new ContaQuery(
            event.getContaId(),
            event.getNumeroConta(),
            event.getDataCriacao(),
            event.getSaldo(),
            event.getLimite(),
            event.getClienteNome(),
            event.getClienteCpf(),
            event.getGerenteCpf(),
            event.getGerenteNome(),
            event.getGerenteEmail()
        );

        contaQueryRepository.save(conta);
    }

    @Transactional
    public void sincronizarContaExcluida(ContaExcluidaEvent event) {
        contaQueryRepository.findByClienteCpf(event.getClienteCpf())
            .ifPresent(contaQueryRepository::delete);
    }

    @Transactional
    public void sincronizarLimiteAlterado(ContaLimiteAlteradoEvent event) {
        ContaQuery conta = contaQueryRepository.findByClienteCpf(event.cpf())
            .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada para o CPF " + event.cpf()));

        conta.setSaldo(event.saldo());
        conta.setLimite(event.limite());
        conta.setGerenteCpf(event.gerenteCpf());
        contaQueryRepository.save(conta);
    }

    @Transactional
    public void execute(MovimentacaoEvent event) {

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new RequisicaoInvalidaException("Evento de movimentação sem eventId");
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

        eventPublisher.publishEvent(event);
        
    }

    private void sincronoizarTransferencia(MovimentacaoEvent event) {
        ContaQuery contaOrigem = contaQueryRepository.findByNumeroConta(event.getNumeroContaOrigem())
            .orElseThrow(() -> new ContaNaoEncontradaException("Conta de origem não encontrada"));
        contaOrigem.setSaldo(event.getNovoSaldoContaOrigem());
        contaQueryRepository.save(contaOrigem);

        ContaQuery contaDestino = contaQueryRepository.findByNumeroConta(event.getNumeroContaDestino())
            .orElseThrow(() -> new ContaNaoEncontradaException("Conta de destino não encontrada"));
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
            .orElseThrow(ContaNaoEncontradaException::new);
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
            .orElseThrow(ContaNaoEncontradaException::new);
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
