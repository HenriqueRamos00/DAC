package br.ufpr.bantads.saga.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.application.dto.event.ClienteAlteracaoFalhouEvent;
import br.ufpr.bantads.saga.application.dto.event.ClientePerfilAlteradoEvent;
import br.ufpr.bantads.saga.application.dto.event.ContaLimiteAlteradoEvent;
import br.ufpr.bantads.saga.services.AlteracaoPerfilOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = "${saga.rabbitmq.queue.alteracao-perfil.response}")
public class AlteracaoPerfilResponseListener {

    private final AlteracaoPerfilOrchestrator orchestrator;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.falha}")
    private String contaAlterarLimiteFalhaRoutingKey;

    @RabbitHandler
    public void handleClientePerfilAlterado(
        ClientePerfilAlteradoEvent event,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Recebido evento da SAGA alteração de perfil com routing-key {}", routingKey);
        orchestrator.handleClientePerfilAlterado(event);
    }

    @RabbitHandler
    public void handleContaLimiteAlterado(
        ContaLimiteAlteradoEvent event,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Recebido evento da SAGA alteração de perfil com routing-key {}", routingKey);
        orchestrator.handleContaLimiteAlterado(event);
    }

    @RabbitHandler
    public void handleAlteracaoFalhou(
        ClienteAlteracaoFalhouEvent event,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Recebido evento da SAGA alteração de perfil com routing-key {}", routingKey);

        if (contaAlterarLimiteFalhaRoutingKey.equals(routingKey)) {
            orchestrator.handleContaAlteracaoLimiteFalhou(event);
            return;
        }

        orchestrator.handleClienteAlteracaoPerfilFalhou(event);
    }
}
