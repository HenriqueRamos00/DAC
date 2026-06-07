package com.ufpr.bantads.auth.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.auth.application.dto.command.ExcluirUsuarioClienteCommand;
import com.ufpr.bantads.auth.application.dto.event.ExclusaoUsuarioClienteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteExcluidoEvent;
import com.ufpr.bantads.auth.application.usecase.ExcluirUsuarioClienteUseCase;
import com.ufpr.bantads.auth.infrastructure.messaging.publisher.CriarUsuarioClienteEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExcluirUsuarioClienteListener {

    private final ExcluirUsuarioClienteUseCase excluirUsuarioClienteUseCase;
    private final CriarUsuarioClienteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.auth.excluir-usuario-cliente.command}")
    public void handle(ExcluirUsuarioClienteCommand command) {
        log.info("Recebido ExcluirUsuarioClienteCommand saga {} cpf {}", command.sagaId(), command.cpf());

        try {
            UsuarioClienteExcluidoEvent event = excluirUsuarioClienteUseCase.execute(command);
            publisher.publishUsuarioClienteExcluido(event);
        } catch (Exception ex) {
            log.warn(
                "Falha ao excluir usuário cliente saga {} cpf {}: {}",
                command.sagaId(),
                command.cpf(),
                ex.getMessage()
            );
            publisher.publishExclusaoUsuarioClienteFalhou(
                new ExclusaoUsuarioClienteFalhouEvent(command.sagaId(), command.cpf(), ex.getMessage())
            );
        }
    }
}
