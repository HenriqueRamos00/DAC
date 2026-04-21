package com.ufpr.bantads.conta.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.event.MovimentacaoEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MovimentacaoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${cqrs.rabbitmq.exchange}")
    private String exchange;

    private final String routingKey = "conta.operacao.movimentacao";

    public void publish(MovimentacaoEvent event) {
        log.info("Publicando evento de movimentação: {}", event);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

}
