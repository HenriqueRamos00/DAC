package br.ufpr.bantads.saga.sagas.alteracaoperfil;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteAlteracaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilRevertidoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteReversaoPerfilFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ContaLimiteAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.AlteracaoUsuarioClienteAuthFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ReversaoUsuarioClienteAuthFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.UsuarioClienteAuthAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.UsuarioClienteAuthRevertidoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.AlteracaoPerfilOrchestrator;
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
    public void handleUsuarioClienteAuthAlterado(
        UsuarioClienteAuthAlteradoEvent event,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Recebido evento da SAGA alteração de perfil com routing-key {}", routingKey);
        orchestrator.handleUsuarioClienteAuthAlterado(event);
    }

    @RabbitHandler
    public void handleAlteracaoUsuarioClienteAuthFalhou(
        AlteracaoUsuarioClienteAuthFalhouEvent event,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Recebida falha do auth na SAGA alteração de perfil com routing-key {}", routingKey);
        orchestrator.handleAlteracaoUsuarioClienteAuthFalhou(event);
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

    @RabbitHandler
    public void handleClientePerfilRevertido(
        ClientePerfilRevertidoEvent event,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Recebido evento de compensação da SAGA alteração de perfil com routing-key {}", routingKey);
        orchestrator.handleClientePerfilRevertido(event);
    }

    @RabbitHandler
    public void handleUsuarioClienteAuthRevertido(
        UsuarioClienteAuthRevertidoEvent event,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Recebido evento de compensação do auth na SAGA alteração de perfil com routing-key {}", routingKey);
        orchestrator.handleUsuarioClienteAuthRevertido(event);
    }

    @RabbitHandler
    public void handleReversaoUsuarioClienteAuthFalhou(
        ReversaoUsuarioClienteAuthFalhouEvent event,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Recebida falha de compensação do auth na SAGA alteração de perfil com routing-key {}", routingKey);
        orchestrator.handleReversaoUsuarioClienteAuthFalhou(event);
    }

    @RabbitHandler
    public void handleClienteReversaoPerfilFalhou(
        ClienteReversaoPerfilFalhouEvent event,
        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Recebida falha de compensação da SAGA alteração de perfil com routing-key {}", routingKey);
        orchestrator.handleClienteReversaoPerfilFalhou(event);
    }
}
