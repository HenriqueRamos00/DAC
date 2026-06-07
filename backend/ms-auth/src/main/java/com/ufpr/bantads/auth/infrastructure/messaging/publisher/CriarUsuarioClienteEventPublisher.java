package com.ufpr.bantads.auth.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.auth.application.dto.event.CriacaoUsuarioClienteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.ExclusaoUsuarioClienteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteCriadoEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteExcluidoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CriarUsuarioClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-cliente.sucesso}")
    private String sucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-cliente.falha}")
    private String falhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.excluir-usuario-cliente.sucesso}")
    private String excluirUsuarioClienteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.excluir-usuario-cliente.falha}")
    private String excluirUsuarioClienteFalhaRoutingKey;

    public void publishUsuarioClienteCriado(UsuarioClienteCriadoEvent event) {
        log.info("Publicando UsuarioClienteCriadoEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, sucessoRoutingKey, event);
    }

    public void publishCriacaoUsuarioClienteFalhou(CriacaoUsuarioClienteFalhouEvent event) {
        log.info("Publicando CriacaoUsuarioClienteFalhouEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, falhaRoutingKey, event);
    }

    public void publishUsuarioClienteExcluido(UsuarioClienteExcluidoEvent event) {
        log.info("Publicando UsuarioClienteExcluidoEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, excluirUsuarioClienteSucessoRoutingKey, event);
    }

    public void publishExclusaoUsuarioClienteFalhou(ExclusaoUsuarioClienteFalhouEvent event) {
        log.info("Publicando ExclusaoUsuarioClienteFalhouEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, excluirUsuarioClienteFalhaRoutingKey, event);
    }
}
