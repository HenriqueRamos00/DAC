package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.command.AlterarLimiteContaCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaLimiteAlteradoEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlterarLimiteContaMockListener {

    private static final BigDecimal LIMITE_PERCENTUAL = new BigDecimal("0.5");

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.sucesso}")
    private String sucessoRoutingKey;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.alterar-limite.command}")
    public void handle(AlterarLimiteContaCommand command) {
        log.info("Mock alteração de limite recebida para CPF {}", command.cpf());

        var salario = command.salario() == null ? BigDecimal.ZERO : command.salario();
        var limite = salario.multiply(LIMITE_PERCENTUAL).setScale(2, RoundingMode.HALF_UP);
        var event = new ContaLimiteAlteradoEvent(
            command.sagaId(),
            command.cpf(),
            "0001-0",
            BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
            limite,
            "00000000000",
            "Gerente Mock"
        );

        rabbitTemplate.convertAndSend(exchange, sucessoRoutingKey, event);
    }
}
