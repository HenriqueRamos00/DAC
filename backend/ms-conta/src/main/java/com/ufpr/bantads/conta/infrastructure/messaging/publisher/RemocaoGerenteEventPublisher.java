package com.ufpr.bantads.conta.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.event.ContasReatribuidasEvent;
import com.ufpr.bantads.conta.application.dto.event.ReatribuicaoContasFalhouEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RemocaoGerenteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.conta.reatribuir-contas-gerente.sucesso}")
    private String reatribuirSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.reatribuir-contas-gerente.falha}")
    private String reatribuirFalhaRoutingKey;

    public void publishContasReatribuidas(ContasReatribuidasEvent event) {
        log.info("Publicando ContasReatribuidasEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, reatribuirSucessoRoutingKey, event);
    }

    public void publishReatribuicaoContasFalhou(ReatribuicaoContasFalhouEvent event) {
        log.info("Publicando ReatribuicaoContasFalhouEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, reatribuirFalhaRoutingKey, event);
    }
}