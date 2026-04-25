package com.ufpr.bantads.cliente.infrastructure.messaging.publisher;

import com.ufpr.bantads.cliente.application.dto.event.ClienteRejeitadoEvent;
import com.ufpr.bantads.cliente.application.dto.event.RejeicaoClienteFalhouEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RejeitarClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.cliente.rejeitar.sucesso}")
    private String rejeitarClienteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.rejeitar.falha}")
    private String rejeitarClienteFalhaRoutingKey;

    public void publishSucesso(ClienteRejeitadoEvent event) {
        log.info("Publicando evento de rejeicao de cliente: {}", event);
        rabbitTemplate.convertAndSend(exchange, rejeitarClienteSucessoRoutingKey, event);
    }

    public void publishFalha(RejeicaoClienteFalhouEvent event) {
        log.info("Publicando evento de falha na rejeicao de cliente: {}", event);
        rabbitTemplate.convertAndSend(exchange, rejeitarClienteFalhaRoutingKey, event);
    }
}
