package com.ufpr.bantads.auth.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.auth.application.dto.command.CriarUsuarioClienteCommand;
import com.ufpr.bantads.auth.application.dto.event.CriacaoUsuarioClienteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteCriadoEvent;
import com.ufpr.bantads.auth.application.dto.request.CriarUsuarioRequest;
import com.ufpr.bantads.auth.application.usecase.CriarUsuarioUseCase;
import com.ufpr.bantads.auth.domain.model.enums.TipoUsuario;
import com.ufpr.bantads.auth.infrastructure.messaging.publisher.CriarUsuarioClienteEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CriarUsuarioClienteListener {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final CriarUsuarioClienteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.auth.criar-usuario-cliente.command}")
    public void handle(CriarUsuarioClienteCommand command) {
        log.info("Recebido CriarUsuarioClienteCommand saga {} cpf {}", command.sagaId(), command.cpf());

        try {
            var response = criarUsuarioUseCase.execute(
                new CriarUsuarioRequest(command.cpf(), command.email(), null, TipoUsuario.CLIENTE)
            );
            publisher.publishUsuarioClienteCriado(UsuarioClienteCriadoEvent.fromResponse(command.sagaId(), response));
        } catch (Exception ex) {
            log.warn("Falha ao criar usuário cliente saga {} cpf {}: {}", command.sagaId(), command.cpf(), ex.getMessage());
            publisher.publishCriacaoUsuarioClienteFalhou(
                new CriacaoUsuarioClienteFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}
