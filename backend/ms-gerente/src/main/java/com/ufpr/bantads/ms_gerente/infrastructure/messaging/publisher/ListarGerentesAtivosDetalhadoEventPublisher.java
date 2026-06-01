package com.ufpr.bantads.ms_gerente.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.ms_gerente.application.dto.event.GerentesAtivosDetalhadosListadosEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.ListagemGerentesAtivosDetalhadosFalhouEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListarGerentesAtivosDetalhadoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos-detalhado.sucesso}")
    private String sucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos-detalhado.falha}")
    private String falhaRoutingKey;

    public void publishGerentesAtivosListados(GerentesAtivosDetalhadosListadosEvent event) {
        log.info("Publicando gerentes ativos detalhados saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, sucessoRoutingKey, event);
    }

    public void publishListagemFalhou(ListagemGerentesAtivosDetalhadosFalhouEvent event) {
        log.info("Publicando falha na listagem de gerentes saga {}", event.sagaId());
        rabbitTemplate.convertAndSend(exchange, falhaRoutingKey, event);
    }
}
