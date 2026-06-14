package com.ufpr.bantads.cliente.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.cliente.application.dto.command.ReverterAlteracaoPerfilClienteCommand;
import com.ufpr.bantads.cliente.application.dto.event.ClientePerfilRevertidoEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClienteReversaoPerfilFalhouEvent;
import com.ufpr.bantads.cliente.application.usecase.ReverterAlteracaoPerfilUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReverterAlteracaoPerfilClienteListener {

    private final RabbitTemplate rabbitTemplate;
    private final ReverterAlteracaoPerfilUseCase reverterAlteracaoPerfilUseCase;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.cliente.reverter-alteracao-perfil.sucesso}")
    private String sucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.reverter-alteracao-perfil.falha}")
    private String falhaRoutingKey;

    @RabbitListener(queues = "${saga.rabbitmq.queue.cliente.reverter-alteracao-perfil.command}")
    public void handle(ReverterAlteracaoPerfilClienteCommand command) {
        String cpf = command == null ? null : command.cpf();
        String sagaId = command == null ? null : command.sagaId();
        log.info("Compensação de alteração de perfil recebida para CPF {}", cpf);

        try {
            ClientePerfilRevertidoEvent event = reverterAlteracaoPerfilUseCase.execute(command);
            rabbitTemplate.convertAndSend(exchange, sucessoRoutingKey, event);
        } catch (Exception ex) {
            log.error("Falha ao compensar perfil do cliente {} na saga {}", cpf, sagaId, ex);
            rabbitTemplate.convertAndSend(
                exchange,
                falhaRoutingKey,
                new ClienteReversaoPerfilFalhouEvent(sagaId, cpf, ex.getMessage())
            );
        }
    }
}
