package com.ufpr.bantads.auth.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.auth.application.dto.event.ExclusaoUsuarioGerenteCompensacaoFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioGerenteExcluidoCompensacaoEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsercaoGerenteCompensacaoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.auth.excluir-usuario-gerente-compensacao.sucesso}")
    private String excluirCompensacaoSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.excluir-usuario-gerente-compensacao.falha}")
    private String excluirCompensacaoFalhaRoutingKey;

    public void publishUsuarioGerenteExcluidoCompensacao(UsuarioGerenteExcluidoCompensacaoEvent event) {
        log.info("Publicando UsuarioGerenteExcluidoCompensacaoEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, excluirCompensacaoSucessoRoutingKey, event);
    }

    public void publishExclusaoUsuarioGerenteCompensacaoFalhou(ExclusaoUsuarioGerenteCompensacaoFalhouEvent event) {
        log.info("Publicando ExclusaoUsuarioGerenteCompensacaoFalhouEvent saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, excluirCompensacaoFalhaRoutingKey, event);
    }
}