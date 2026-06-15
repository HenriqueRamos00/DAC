package com.ufpr.bantads.auth.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.auth.application.dto.event.AlteracaoUsuarioClienteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.ReversaoUsuarioClienteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteAlteradoEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteRevertidoEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlterarUsuarioClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.auth.alterar-usuario-cliente.sucesso}")
    private String alterarSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.alterar-usuario-cliente.falha}")
    private String alterarFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.reverter-alteracao-usuario-cliente.sucesso}")
    private String reverterSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.reverter-alteracao-usuario-cliente.falha}")
    private String reverterFalhaRoutingKey;

    public void publishUsuarioClienteAlterado(UsuarioClienteAlteradoEvent event) {
        log.info("Publicando UsuarioClienteAlteradoEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, alterarSucessoRoutingKey, event);
    }

    public void publishAlteracaoUsuarioClienteFalhou(AlteracaoUsuarioClienteFalhouEvent event) {
        log.info("Publicando AlteracaoUsuarioClienteFalhouEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, alterarFalhaRoutingKey, event);
    }

    public void publishUsuarioClienteRevertido(UsuarioClienteRevertidoEvent event) {
        log.info("Publicando UsuarioClienteRevertidoEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, reverterSucessoRoutingKey, event);
    }

    public void publishReversaoUsuarioClienteFalhou(ReversaoUsuarioClienteFalhouEvent event) {
        log.info("Publicando ReversaoUsuarioClienteFalhouEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, reverterFalhaRoutingKey, event);
    }
}
