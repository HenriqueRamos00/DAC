package com.ufpr.bantads.cliente.infrastructure.messaging.publisher;

import com.ufpr.bantads.cliente.application.dto.event.AprovacaoClienteFalhouEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClienteAprovadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AprovarClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.cliente.aprovar.sucesso}")
    private String aprovarClienteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.aprovar.falha}")
    private String aprovarClienteFalhaRoutingKey;

    public void publishSucesso(ClienteAprovadoEvent event) {
        log.info("Publicando evento de aprovacao de cliente: {}", event);
        rabbitTemplate.convertAndSend(exchange, aprovarClienteSucessoRoutingKey, event);
    }

    public void publishFalha(AprovacaoClienteFalhouEvent event) {
        log.info("Publicando evento de falha na aprovacao de cliente: {}", event);
        rabbitTemplate.convertAndSend(exchange, aprovarClienteFalhaRoutingKey, event);
    }
}
