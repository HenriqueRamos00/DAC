package com.ufpr.bantads.conta.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.event.AtribuicaoGerenteContaFalhouEvent;
import com.ufpr.bantads.conta.application.dto.event.ConsultaGerenteMaisContasFalhouEvent;
import com.ufpr.bantads.conta.application.dto.event.GerenteAtribuidoContaEvent;
import com.ufpr.bantads.conta.application.dto.event.GerenteMaisContasConsultadoEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsercaoGerenteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.conta.consultar-gerente-mais-contas.sucesso}")
    private String consultarSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.consultar-gerente-mais-contas.falha}")
    private String consultarFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.atribuir-gerente.sucesso}")
    private String atribuirSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.atribuir-gerente.falha}")
    private String atribuirFalhaRoutingKey;

    public void publishGerenteMaisContasConsultado(GerenteMaisContasConsultadoEvent event) {
        log.info("Publicando GerenteMaisContasConsultadoEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, consultarSucessoRoutingKey, event);
    }

    public void publishConsultaGerenteMaisContasFalhou(ConsultaGerenteMaisContasFalhouEvent event) {
        log.info("Publicando ConsultaGerenteMaisContasFalhouEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, consultarFalhaRoutingKey, event);
    }

    public void publishGerenteAtribuidoConta(GerenteAtribuidoContaEvent event) {
        log.info("Publicando GerenteAtribuidoContaEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, atribuirSucessoRoutingKey, event);
    }

    public void publishAtribuicaoGerenteContaFalhou(AtribuicaoGerenteContaFalhouEvent event) {
        log.info("Publicando AtribuicaoGerenteContaFalhouEvent: {}", event);
        rabbitTemplate.convertAndSend(exchange, atribuirFalhaRoutingKey, event);
    }
}