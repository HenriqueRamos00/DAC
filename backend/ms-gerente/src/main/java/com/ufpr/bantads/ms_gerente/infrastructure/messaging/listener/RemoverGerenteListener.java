package com.ufpr.bantads.ms_gerente.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.ms_gerente.application.dto.command.RemoverGerenteCommand;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteRemovidoEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.RemocaoGerenteFalhouEvent;
import com.ufpr.bantads.ms_gerente.application.usecase.DeleteGerenteUseCase;
import com.ufpr.bantads.ms_gerente.domain.exception.GerenteNaoEncontradoException;
import com.ufpr.bantads.ms_gerente.infrastructure.messaging.publisher.RemocaoGerenteEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RemoverGerenteListener {

    private static final String MOTIVO_GERENTE_NAO_ENCONTRADO = "GERENTE_NAO_ENCONTRADO";

    private final DeleteGerenteUseCase deleteGerenteUseCase;
    private final RemocaoGerenteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.gerente.remover.command}")
    public void handle(RemoverGerenteCommand command) {
        log.info("Recebido RemoverGerenteCommand saga {} cpf {}", command.sagaId(), command.cpf());

        try {
            deleteGerenteUseCase.execute(command.cpf());
            publisher.publishGerenteRemovido(new GerenteRemovidoEvent(command.sagaId(), command.cpf()));
        } catch (GerenteNaoEncontradoException ex) {
            log.warn("Gerente não encontrado saga {}: {}", command.sagaId(), ex.getMessage());
            publisher.publishRemocaoGerenteFalhou(
                new RemocaoGerenteFalhouEvent(command.sagaId(), MOTIVO_GERENTE_NAO_ENCONTRADO)
            );
        } catch (Exception ex) {
            log.error("Falha ao remover gerente saga {}", command.sagaId(), ex);
            publisher.publishRemocaoGerenteFalhou(
                new RemocaoGerenteFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}