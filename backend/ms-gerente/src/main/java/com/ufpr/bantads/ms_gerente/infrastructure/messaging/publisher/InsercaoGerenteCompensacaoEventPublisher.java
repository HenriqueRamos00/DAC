package com.ufpr.bantads.ms_gerente.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteRemovidoCompensacaoEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.RemocaoGerenteCompensacaoFalhouEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsercaoGerenteCompensacaoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.gerente.remover-compensacao.sucesso}")
    private String removerCompensacaoSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.remover-compensacao.falha}")
    private String removerCompensacaoFalhaRoutingKey;

    public void publishGerenteRemovidoCompensacao(GerenteRemovidoCompensacaoEvent event) {
        log.info("Publicando GerenteRemovidoCompensacaoEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, removerCompensacaoSucessoRoutingKey, event);
    }

    public void publishRemocaoGerenteCompensacaoFalhou(RemocaoGerenteCompensacaoFalhouEvent event) {
        log.info("Publicando RemocaoGerenteCompensacaoFalhouEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, removerCompensacaoFalhaRoutingKey, event);
    }
}