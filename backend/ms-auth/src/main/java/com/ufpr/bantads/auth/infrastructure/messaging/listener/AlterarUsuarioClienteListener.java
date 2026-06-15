package com.ufpr.bantads.auth.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.auth.application.dto.command.AlterarUsuarioClienteCommand;
import com.ufpr.bantads.auth.application.dto.command.ReverterAlteracaoUsuarioClienteCommand;
import com.ufpr.bantads.auth.application.dto.event.AlteracaoUsuarioClienteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.ReversaoUsuarioClienteFalhouEvent;
import com.ufpr.bantads.auth.application.usecase.AlterarUsuarioClienteUseCase;
import com.ufpr.bantads.auth.infrastructure.messaging.publisher.AlterarUsuarioClienteEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlterarUsuarioClienteListener {

    private final AlterarUsuarioClienteUseCase useCase;
    private final AlterarUsuarioClienteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.auth.alterar-usuario-cliente.command}")
    public void handleAlterar(AlterarUsuarioClienteCommand command) {
        log.info("Recebido AlterarUsuarioClienteCommand saga {} cpf {}", command.sagaId(), command.cpf());

        try {
            publisher.publishUsuarioClienteAlterado(useCase.alterar(command));
        } catch (Exception ex) {
            log.warn("Falha ao alterar usuário cliente saga {} cpf {}: {}", command.sagaId(), command.cpf(), ex.getMessage());
            publisher.publishAlteracaoUsuarioClienteFalhou(
                new AlteracaoUsuarioClienteFalhouEvent(command.sagaId(), command.cpf(), ex.getMessage())
            );
        }
    }

    @RabbitListener(queues = "${saga.rabbitmq.queue.auth.reverter-alteracao-usuario-cliente.command}")
    public void handleReverter(ReverterAlteracaoUsuarioClienteCommand command) {
        log.info("Recebido ReverterAlteracaoUsuarioClienteCommand saga {} cpf {}", command.sagaId(), command.cpf());

        try {
            publisher.publishUsuarioClienteRevertido(useCase.reverter(command));
        } catch (Exception ex) {
            log.warn("Falha ao reverter usuário cliente saga {} cpf {}: {}", command.sagaId(), command.cpf(), ex.getMessage());
            publisher.publishReversaoUsuarioClienteFalhou(
                new ReversaoUsuarioClienteFalhouEvent(command.sagaId(), command.cpf(), ex.getMessage())
            );
        }
    }
}
