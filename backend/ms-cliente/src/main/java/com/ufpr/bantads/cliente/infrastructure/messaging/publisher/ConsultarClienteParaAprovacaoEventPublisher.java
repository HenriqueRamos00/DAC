package com.ufpr.bantads.cliente.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.cliente.application.dto.event.ClienteConsultadoParaAprovacaoEvent;
import com.ufpr.bantads.cliente.application.dto.event.ConsultaClienteParaAprovacaoFalhouEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConsultarClienteParaAprovacaoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.cliente.consultar-para-aprovacao.sucesso}")
    private String consultarClienteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.consultar-para-aprovacao.falha}")
    private String consultarClienteFalhaRoutingKey;

    public void publishSucesso(ClienteConsultadoParaAprovacaoEvent event) {
        log.info("Publicando cliente consultado para aprovacao: {}", event);
        rabbitTemplate.convertAndSend(exchange, consultarClienteSucessoRoutingKey, event);
    }

    public void publishFalha(ConsultaClienteParaAprovacaoFalhouEvent event) {
        log.info("Publicando falha na consulta de cliente para aprovacao: {}", event);
        rabbitTemplate.convertAndSend(exchange, consultarClienteFalhaRoutingKey, event);
    }
}
