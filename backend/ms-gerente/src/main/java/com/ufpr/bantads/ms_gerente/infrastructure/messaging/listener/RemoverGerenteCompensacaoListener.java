package com.ufpr.bantads.ms_gerente.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.ms_gerente.application.dto.command.RemoverGerenteCompensacaoCommand;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteRemovidoCompensacaoEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.RemocaoGerenteCompensacaoFalhouEvent;
import com.ufpr.bantads.ms_gerente.application.usecase.DeleteGerenteUseCase;
import com.ufpr.bantads.ms_gerente.domain.exception.GerenteNaoEncontradoException;
import com.ufpr.bantads.ms_gerente.infrastructure.messaging.publisher.InsercaoGerenteCompensacaoEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RemoverGerenteCompensacaoListener {

    private final DeleteGerenteUseCase deleteGerenteUseCase;
    private final InsercaoGerenteCompensacaoEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.gerente.remover-compensacao.command}")
    public void handle(RemoverGerenteCompensacaoCommand command) {
        log.info("Recebido RemoverGerenteCompensacaoCommand saga {} cpf {}", command.sagaId(), command.cpf());

        try {
            deleteGerenteUseCase.execute(command.cpf());
            publisher.publishGerenteRemovidoCompensacao(
                new GerenteRemovidoCompensacaoEvent(command.sagaId(), command.cpf())
            );
        } catch (GerenteNaoEncontradoException ex) {
            // Compensação idempotente: gerente já não existe → tratamos como sucesso
            log.warn("Gerente não encontrado na compensação saga {}, tratando como sucesso: {}",
                command.sagaId(), ex.getMessage());
            publisher.publishGerenteRemovidoCompensacao(
                new GerenteRemovidoCompensacaoEvent(command.sagaId(), command.cpf())
            );
        } catch (Exception ex) {
            log.error("Falha ao remover gerente na compensação saga {}", command.sagaId(), ex);
            publisher.publishRemocaoGerenteCompensacaoFalhou(
                new RemocaoGerenteCompensacaoFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}