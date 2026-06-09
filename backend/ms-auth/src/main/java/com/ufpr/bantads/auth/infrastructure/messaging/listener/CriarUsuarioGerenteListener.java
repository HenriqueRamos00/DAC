package com.ufpr.bantads.auth.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.auth.application.dto.command.CriarUsuarioGerenteCommand;
import com.ufpr.bantads.auth.application.dto.event.CriacaoUsuarioGerenteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioGerenteCriadoEvent;
import com.ufpr.bantads.auth.application.dto.request.CriarUsuarioRequest;
import com.ufpr.bantads.auth.application.usecase.CriarUsuarioUseCase;
import com.ufpr.bantads.auth.domain.model.enums.TipoUsuario;
import com.ufpr.bantads.auth.infrastructure.messaging.publisher.CriarUsuarioGerenteEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CriarUsuarioGerenteListener {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final CriarUsuarioGerenteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.auth.criar-usuario-gerente.command}")
    public void handle(CriarUsuarioGerenteCommand command) {
        log.info("Recebido CriarUsuarioGerenteCommand saga {} cpf {}", command.sagaId(), command.cpf());

        try {
            var response = criarUsuarioUseCase.execute(
                new CriarUsuarioRequest(command.cpf(), command.email(), command.senha(), TipoUsuario.GERENTE)
            );
            publisher.publishUsuarioGerenteCriado(UsuarioGerenteCriadoEvent.fromResponse(command.sagaId(), response));
        } catch (Exception ex) {
            log.warn("Falha ao criar usuário gerente saga {} cpf {}: {}", command.sagaId(), command.cpf(), ex.getMessage());
            publisher.publishCriacaoUsuarioGerenteFalhou(
                new CriacaoUsuarioGerenteFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}