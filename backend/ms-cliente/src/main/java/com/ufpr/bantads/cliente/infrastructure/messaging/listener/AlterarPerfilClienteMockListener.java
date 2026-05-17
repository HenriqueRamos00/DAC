package com.ufpr.bantads.cliente.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.cliente.application.dto.command.AlterarPerfilClienteCommand;
import com.ufpr.bantads.cliente.application.dto.event.ClientePerfilAlteradoEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlterarPerfilClienteMockListener {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.cliente.alterar-perfil.sucesso}")
    private String sucessoRoutingKey;

    @RabbitListener(queues = "${saga.rabbitmq.queue.cliente.alterar-perfil.command}")
    public void handle(AlterarPerfilClienteCommand command) {
        log.info("Mock alteração de perfil recebida para CPF {}", command.cpf());

        var event = new ClientePerfilAlteradoEvent(
            command.sagaId(),
            command.cpf(),
            command.nome(),
            command.email(),
            command.telefone(),
            command.salario(),
            command.cep(),
            command.logradouro(),
            command.cidade(),
            command.estado(),
            command.complemento(),
            command.numero()
        );

        rabbitTemplate.convertAndSend(exchange, sucessoRoutingKey, event);
    }
}
