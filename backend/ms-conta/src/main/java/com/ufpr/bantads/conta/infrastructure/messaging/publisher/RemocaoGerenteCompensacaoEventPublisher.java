package com.ufpr.bantads.conta.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.event.ReatribuicaoContasRevertidaCompensacaoEvent;
import com.ufpr.bantads.conta.application.dto.event.ReversaoReatribuicaoContasCompensacaoFalhouEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RemocaoGerenteCompensacaoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.conta.reverter-reatribuicao-contas-compensacao.sucesso}")
    private String reverterCompensacaoSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.reverter-reatribuicao-contas-compensacao.falha}")
    private String reverterCompensacaoFalhaRoutingKey;

    public void publishReatribuicaoContasRevertidaCompensacao(ReatribuicaoContasRevertidaCompensacaoEvent event) {
        log.info("Publicando ReatribuicaoContasRevertidaCompensacaoEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, reverterCompensacaoSucessoRoutingKey, event);
    }

    public void publishReversaoReatribuicaoContasCompensacaoFalhou(ReversaoReatribuicaoContasCompensacaoFalhouEvent event) {
        log.info("Publicando ReversaoReatribuicaoContasCompensacaoFalhouEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, reverterCompensacaoFalhaRoutingKey, event);
    }
}