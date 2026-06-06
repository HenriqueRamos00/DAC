package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.command.ExcluirContaClienteCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaClienteExcluidaEvent;
import com.ufpr.bantads.conta.application.dto.event.ContaExcluidaEvent;
import com.ufpr.bantads.conta.application.dto.event.ExclusaoContaClienteFalhouEvent;
import com.ufpr.bantads.conta.application.usecase.ExcluirContaClienteUseCase;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.ContaEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExcluirContaClienteListener {

    private final ExcluirContaClienteUseCase excluirContaClienteUseCase;
    private final ContaEventPublisher contaEventPublisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.excluir-conta-cliente.command}")
    public void handle(ExcluirContaClienteCommand command) {
        log.info("Recebido comando de exclusão de conta para CPF {}", command.clienteCpf());

        try {
            ContaExcluidaEvent event = excluirContaClienteUseCase.execute(command);
            contaEventPublisher.publishContaClienteExcluida(
                new ContaClienteExcluidaEvent(event.getSagaId(), event.getClienteCpf(), event.getNumeroConta())
            );
        } catch (Exception ex) {
            log.warn("Falha ao excluir conta para CPF {}: {}", command.clienteCpf(), ex.getMessage());
            contaEventPublisher.publishExclusaoContaClienteFalhou(
                new ExclusaoContaClienteFalhouEvent(command.sagaId(), command.clienteCpf(), ex.getMessage())
            );
        }
    }
}
