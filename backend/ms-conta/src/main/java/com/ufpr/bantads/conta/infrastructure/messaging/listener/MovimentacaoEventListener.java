package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.event.ContaCriadaEvent;
import com.ufpr.bantads.conta.application.dto.event.ContaLimiteAlteradoEvent;
import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.application.usecase.SyncMovimentacaoUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = "${cqrs.rabbitmq.queue}")
public class MovimentacaoEventListener {

    private final SyncMovimentacaoUseCase syncMovimentacaoUseCase;

    @RabbitHandler
    public void handle(MovimentacaoEvent event) {
        log.info("Recebido evento CQRS de movimentação: {}", event);
        syncMovimentacaoUseCase.execute(event);
    }

    @RabbitHandler
    public void handle(ContaCriadaEvent event) {
        log.info("Recebido evento CQRS de conta criada: {}", event);
        syncMovimentacaoUseCase.sincronizarContaCriada(event);
    }

    @RabbitHandler
    public void handle(ContaLimiteAlteradoEvent event) {
        log.info("Recebido evento CQRS de limite alterado: {}", event);
        syncMovimentacaoUseCase.sincronizarLimiteAlterado(event);
    }

}
