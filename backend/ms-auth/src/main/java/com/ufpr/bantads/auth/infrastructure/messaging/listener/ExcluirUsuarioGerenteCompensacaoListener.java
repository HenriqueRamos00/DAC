package com.ufpr.bantads.auth.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.auth.application.dto.command.ExcluirUsuarioGerenteCompensacaoCommand;
import com.ufpr.bantads.auth.application.dto.event.ExclusaoUsuarioGerenteCompensacaoFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioGerenteExcluidoCompensacaoEvent;
import com.ufpr.bantads.auth.application.usecase.ExcluirUsuarioGerenteUseCase;
import com.ufpr.bantads.auth.infrastructure.messaging.publisher.InsercaoGerenteCompensacaoEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExcluirUsuarioGerenteCompensacaoListener {

    private final ExcluirUsuarioGerenteUseCase excluirUsuarioGerenteUseCase;
    private final InsercaoGerenteCompensacaoEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.auth.excluir-usuario-gerente-compensacao.command}")
    public void handle(ExcluirUsuarioGerenteCompensacaoCommand command) {
        log.info("Recebido ExcluirUsuarioGerenteCompensacaoCommand saga {} cpf {}", command.sagaId(), command.cpf());

        try {
            UsuarioGerenteExcluidoCompensacaoEvent event = excluirUsuarioGerenteUseCase.execute(command);
            publisher.publishUsuarioGerenteExcluidoCompensacao(event);
        } catch (Exception ex) {
            log.warn(
                "Falha ao excluir usuário gerente na compensação saga {} cpf {}: {}",
                command.sagaId(),
                command.cpf(),
                ex.getMessage()
            );
            publisher.publishExclusaoUsuarioGerenteCompensacaoFalhou(
                new ExclusaoUsuarioGerenteCompensacaoFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}