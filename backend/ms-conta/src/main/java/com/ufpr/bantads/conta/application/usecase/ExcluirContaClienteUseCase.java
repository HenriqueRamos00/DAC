package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.command.ExcluirContaClienteCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaExcluidaEvent;
import com.ufpr.bantads.conta.domain.exception.RegraNegocioException;
import com.ufpr.bantads.conta.domain.exception.RequisicaoInvalidaException;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.MovimentacaoQueryRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.ContaEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcluirContaClienteUseCase {

    private final ContaCommandRepository contaCommandRepository;
    private final MovimentacaoQueryRepository movimentacaoQueryRepository;
    private final ContaEventPublisher contaEventPublisher;

    @Transactional
    public ContaExcluidaEvent execute(ExcluirContaClienteCommand command) {
        validar(command);

        ContaCommand conta = contaCommandRepository.findByClienteCpf(command.clienteCpf()).orElse(null);

        if (conta == null) {
            ContaExcluidaEvent event = toEvent(command, command.numeroConta());
            contaEventPublisher.publishContaExcluidaCqrs(event);
            return event;
        }

        validarMesmaConta(command, conta);
        validarContaSemUso(conta);

        contaCommandRepository.delete(conta);

        ContaExcluidaEvent event = toEvent(command, conta.getNumeroConta());
        contaEventPublisher.publishContaExcluidaCqrs(event);

        return event;
    }

    private void validar(ExcluirContaClienteCommand command) {
        if (command == null) {
            throw new RequisicaoInvalidaException("Dados da exclusão de conta são obrigatórios");
        }

        if (command.clienteCpf() == null || command.clienteCpf().isBlank()) {
            throw new RequisicaoInvalidaException("CPF do cliente é obrigatório");
        }
    }

    private void validarMesmaConta(ExcluirContaClienteCommand command, ContaCommand conta) {
        if (command.numeroConta() == null || command.numeroConta().isBlank()) {
            return;
        }

        if (!command.numeroConta().equals(conta.getNumeroConta())) {
            throw new RegraNegocioException("Número da conta não pertence ao CPF informado");
        }
    }

    private void validarContaSemUso(ContaCommand conta) {
        if (conta.getSaldo() != null && conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new RegraNegocioException("Conta possui saldo diferente de zero");
        }

        boolean possuiMovimentacao = !movimentacaoQueryRepository
            .findByContaOrigemNumeroOrContaDestinoNumero(conta.getNumeroConta(), conta.getNumeroConta())
            .isEmpty();

        if (possuiMovimentacao) {
            throw new RegraNegocioException("Conta possui movimentações");
        }
    }

    private ContaExcluidaEvent toEvent(ExcluirContaClienteCommand command, String numeroConta) {
        return ContaExcluidaEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("CONTA_EXCLUIDA")
            .eventDate(LocalDateTime.now())
            .sagaId(command.sagaId())
            .clienteCpf(command.clienteCpf())
            .numeroConta(numeroConta)
            .build();
    }
}
