package com.ufpr.bantads.ms_gerente.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteInseridoEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.InsercaoGerenteFalhouEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsercaoGerenteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.gerente.inserir.sucesso}")
    private String sucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.inserir.falha}")
    private String falhaRoutingKey;

    public void publishGerenteInserido(GerenteInseridoEvent event) {
        log.info("Publicando GerenteInseridoEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, sucessoRoutingKey, event);
    }

    public void publishInsercaoGerenteFalhou(InsercaoGerenteFalhouEvent event) {
        log.info("Publicando InsercaoGerenteFalhouEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, falhaRoutingKey, event);
    }
}