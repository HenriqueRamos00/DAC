package com.ufpr.bantads.auth.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.auth.application.dto.event.CriacaoUsuarioGerenteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioGerenteCriadoEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CriarUsuarioGerenteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-gerente.sucesso}")
    private String sucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-gerente.falha}")
    private String falhaRoutingKey;

    public void publishUsuarioGerenteCriado(UsuarioGerenteCriadoEvent event) {
        log.info("Publicando UsuarioGerenteCriadoEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, sucessoRoutingKey, event);
    }

    public void publishCriacaoUsuarioGerenteFalhou(CriacaoUsuarioGerenteFalhouEvent event) {
        log.info("Publicando CriacaoUsuarioGerenteFalhouEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, falhaRoutingKey, event);
    }
}