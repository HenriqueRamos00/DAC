package com.ufpr.bantads.cliente.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.cliente.application.dto.command.AlterarPerfilClienteCommand;
import com.ufpr.bantads.cliente.application.dto.event.ClienteAlteracaoFalhouEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClientePerfilAlteradoEvent;
import com.ufpr.bantads.cliente.application.usecase.AlterarPerfilUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlterarPerfilClienteListener {

    private final RabbitTemplate rabbitTemplate;
    private final AlterarPerfilUseCase alterarPerfilUseCase;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.cliente.alterar-perfil.sucesso}")
    private String sucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.alterar-perfil.falha}")
    private String falhaRoutingKey;

    @RabbitListener(queues = "${saga.rabbitmq.queue.cliente.alterar-perfil.command}")
    public void handle(AlterarPerfilClienteCommand command) {
        log.info("Alteração de perfil recebida para CPF {}", command.cpf());

        try {
            ClientePerfilAlteradoEvent event = alterarPerfilUseCase.execute(
                command.sagaId(),
                command.cpf(),
                command);

            rabbitTemplate.convertAndSend(exchange, sucessoRoutingKey, event);
        } catch (Exception ex) {
            log.error("Falha ao alterar perfil do cliente {} na saga {}", command.cpf(), command.sagaId(), ex);
            rabbitTemplate.convertAndSend(
                exchange,
                falhaRoutingKey,
                new ClienteAlteracaoFalhouEvent(command.sagaId(), command.cpf(), ex.getMessage())
            );
        }
    }
}
