package com.ufpr.bantads.conta.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.event.ContaAlteracaoLimiteFalhouEvent;
import com.ufpr.bantads.conta.application.dto.event.ContaCriadaEvent;
import com.ufpr.bantads.conta.application.dto.event.ContaCriadaSagaEvent;
import com.ufpr.bantads.conta.application.dto.event.ContaLimiteAlteradoEvent;
import com.ufpr.bantads.conta.application.dto.event.CriacaoContaFalhouEvent;
import com.ufpr.bantads.conta.application.dto.event.GerenteParaNovaContaSelecionadoEvent;
import com.ufpr.bantads.conta.application.dto.event.SelecaoGerenteParaNovaContaFalhouEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ContaEventPublisher {

    private static final String CONTA_CRIADA_CQRS_ROUTING_KEY = "conta.operacao.criada";
    private static final String CONTA_LIMITE_ALTERADO_CQRS_ROUTING_KEY = "conta.operacao.limite-alterado";

    private final RabbitTemplate rabbitTemplate;

    @Value("${cqrs.rabbitmq.exchange}")
    private String cqrsExchange;

    @Value("${saga.rabbitmq.exchange}")
    private String sagaExchange;

    @Value("${saga.rabbitmq.routing-key.conta.criar.sucesso}")
    private String criarContaSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.criar.falha}")
    private String criarContaFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.sucesso}")
    private String alterarLimiteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.falha}")
    private String alterarLimiteFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.selecionar-gerente-para-nova-conta.sucesso}")
    private String selecionarGerenteParaNovaContaSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.selecionar-gerente-para-nova-conta.falha}")
    private String selecionarGerenteParaNovaContaFalhaRoutingKey;

    public void publishContaCriadaCqrs(ContaCriadaEvent event) {
        log.info("Publicando evento CQRS de conta criada: {}", event);
        rabbitTemplate.convertAndSend(cqrsExchange, CONTA_CRIADA_CQRS_ROUTING_KEY, event);
    }

    public void publishContaCriadaSaga(ContaCriadaEvent event) {
        ContaCriadaSagaEvent sagaEvent = ContaCriadaSagaEvent.from(event);

        log.info("Publicando sucesso de criação de conta para saga: {}", sagaEvent);
        rabbitTemplate.convertAndSend(sagaExchange, criarContaSucessoRoutingKey, sagaEvent);
    }

    public void publishCriacaoContaFalhou(CriacaoContaFalhouEvent event) {
        log.info("Publicando falha de criação de conta para saga: {}", event);
        rabbitTemplate.convertAndSend(sagaExchange, criarContaFalhaRoutingKey, event);
    }

    public void publishLimiteAlteradoCqrs(ContaLimiteAlteradoEvent event) {
        log.info("Publicando evento CQRS de limite alterado: {}", event);
        rabbitTemplate.convertAndSend(cqrsExchange, CONTA_LIMITE_ALTERADO_CQRS_ROUTING_KEY, event);
    }

    public void publishLimiteAlteradoSaga(ContaLimiteAlteradoEvent event) {
        log.info("Publicando sucesso de alteração de limite para saga: {}", event);
        rabbitTemplate.convertAndSend(sagaExchange, alterarLimiteSucessoRoutingKey, event);
    }

    public void publishAlteracaoLimiteFalhou(ContaAlteracaoLimiteFalhouEvent event) {
        log.info("Publicando falha de alteração de limite para saga: {}", event);
        rabbitTemplate.convertAndSend(sagaExchange, alterarLimiteFalhaRoutingKey, event);
    }

    public void publishGerenteParaNovaContaSelecionado(GerenteParaNovaContaSelecionadoEvent event) {
        log.info("Publicando gerente para nova conta selecionado: {}", event);
        rabbitTemplate.convertAndSend(sagaExchange, selecionarGerenteParaNovaContaSucessoRoutingKey, event);
    }

    public void publishSelecaoGerenteParaNovaContaFalhou(SelecaoGerenteParaNovaContaFalhouEvent event) {
        log.info("Publicando falha de seleção de gerente para saga: {}", event);
        rabbitTemplate.convertAndSend(sagaExchange, selecionarGerenteParaNovaContaFalhaRoutingKey, event);
    }
}
