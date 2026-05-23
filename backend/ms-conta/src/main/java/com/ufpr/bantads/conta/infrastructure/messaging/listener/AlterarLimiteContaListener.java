package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.command.AlterarLimiteContaCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaAlteracaoLimiteFalhouEvent;
import com.ufpr.bantads.conta.application.dto.event.ContaLimiteAlteradoEvent;
import com.ufpr.bantads.conta.application.usecase.AlterarLimiteUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlterarLimiteContaListener {

    private final RabbitTemplate rabbitTemplate;
    private final AlterarLimiteUseCase useCase;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.sucesso}")
    private String sucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.falha}")
    private String falhaRoutingKey;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.alterar-limite.command}")
    public void handle(AlterarLimiteContaCommand command) {
        log.info("Alteração de limite recebida para CPF {}", command.cpf());

        try {
            ContaLimiteAlteradoEvent event = useCase.execute(command);
            rabbitTemplate.convertAndSend(exchange, sucessoRoutingKey, event);
        } catch (Exception ex) {
            log.error("Falha ao alterar limite da conta do cliente {} na saga {}", command.cpf(), command.sagaId(), ex);
            rabbitTemplate.convertAndSend(
                exchange,
                falhaRoutingKey,
                new ContaAlteracaoLimiteFalhouEvent(command.sagaId(), command.cpf(), ex.getMessage())
            );
        }
    }
}
