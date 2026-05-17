package br.ufpr.bantads.saga.infrastructure.messaging.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.application.dto.event.ClienteAlteracaoFalhouEvent;
import br.ufpr.bantads.saga.application.dto.event.ClientePerfilAlteradoEvent;
import br.ufpr.bantads.saga.application.dto.event.ContaLimiteAlteradoEvent;
import br.ufpr.bantads.saga.services.AlteracaoPerfilOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlteracaoPerfilResponseListener {

    private final AlteracaoPerfilOrchestrator orchestrator;
    private final ObjectMapper objectMapper;

    @Value("${saga.rabbitmq.routing-key.cliente.alterar-perfil.sucesso}")
    private String clienteAlterarPerfilSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.alterar-perfil.falha}")
    private String clienteAlterarPerfilFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.sucesso}")
    private String contaAlterarLimiteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.falha}")
    private String contaAlterarLimiteFalhaRoutingKey;

    @RabbitListener(queues = "${saga.rabbitmq.queue.alteracao-perfil.response}")
    public void handle(Message message) throws Exception {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        byte[] body = message.getBody();

        log.info("Recebido evento da SAGA alteração de perfil com routing-key {}", routingKey);

        if (clienteAlterarPerfilSucessoRoutingKey.equals(routingKey)) {
            orchestrator.handleClientePerfilAlterado(objectMapper.readValue(body, ClientePerfilAlteradoEvent.class));
            return;
        }

        if (clienteAlterarPerfilFalhaRoutingKey.equals(routingKey)) {
            orchestrator.handleClienteAlteracaoPerfilFalhou(objectMapper.readValue(body, ClienteAlteracaoFalhouEvent.class));
            return;
        }

        if (contaAlterarLimiteSucessoRoutingKey.equals(routingKey)) {
            orchestrator.handleContaLimiteAlterado(objectMapper.readValue(body, ContaLimiteAlteradoEvent.class));
            return;
        }

        if (contaAlterarLimiteFalhaRoutingKey.equals(routingKey)) {
            orchestrator.handleContaAlteracaoLimiteFalhou(objectMapper.readValue(body, ClienteAlteracaoFalhouEvent.class));
            return;
        }

        log.warn("Routing-key não tratada na SAGA alteração de perfil: {}", routingKey);
    }
}
