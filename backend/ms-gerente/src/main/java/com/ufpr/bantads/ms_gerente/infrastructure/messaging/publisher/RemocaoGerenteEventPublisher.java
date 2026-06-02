package com.ufpr.bantads.ms_gerente.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteRemovidoEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerentesAtivosListadosEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.ListagemGerentesAtivosFalhouEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.RemocaoGerenteFalhouEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RemocaoGerenteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos.sucesso}")
    private String listarAtivosSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos.falha}")
    private String listarAtivosFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.remover.sucesso}")
    private String removerSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.remover.falha}")
    private String removerFalhaRoutingKey;

    public void publishGerentesAtivosListados(GerentesAtivosListadosEvent event) {
        log.info("Publicando GerentesAtivosListadosEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, listarAtivosSucessoRoutingKey, event);
    }

    public void publishListagemGerentesAtivosFalhou(ListagemGerentesAtivosFalhouEvent event) {
        log.info("Publicando ListagemGerentesAtivosFalhouEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, listarAtivosFalhaRoutingKey, event);
    }

    public void publishGerenteRemovido(GerenteRemovidoEvent event) {
        log.info("Publicando GerenteRemovidoEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, removerSucessoRoutingKey, event);
    }

    public void publishRemocaoGerenteFalhou(RemocaoGerenteFalhouEvent event) {
        log.info("Publicando RemocaoGerenteFalhouEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, removerFalhaRoutingKey, event);
    }
}