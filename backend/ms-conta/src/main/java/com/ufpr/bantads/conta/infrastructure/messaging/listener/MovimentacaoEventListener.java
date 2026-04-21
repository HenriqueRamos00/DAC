package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;
import com.ufpr.bantads.conta.application.usecase.SyncMovimentacaoUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovimentacaoEventListener {

    private final SyncMovimentacaoUseCase syncMovimentacaoUseCase;

    @Value("${cqrs.rabbitmq.queue}")
    private String queue;

    @RabbitListener(queues = "${cqrs.rabbitmq.queue}")
    public void receiveMessage(MovimentacaoEvent message) {
        log.info("Recebendo mensagem de movimentação na fila {}: {}", queue, message);
        syncMovimentacaoUseCase.execute(message);
    }

}
