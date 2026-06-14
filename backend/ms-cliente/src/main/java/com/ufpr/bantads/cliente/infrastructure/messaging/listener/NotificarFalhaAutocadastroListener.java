package com.ufpr.bantads.cliente.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.cliente.application.dto.command.NotificarFalhaAutocadastroCommand;
import com.ufpr.bantads.cliente.application.usecase.GetClienteByCpfUseCase;
import com.ufpr.bantads.cliente.application.usecase.NotificarClienteEmailUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificarFalhaAutocadastroListener {

    private final GetClienteByCpfUseCase getClienteByCpfUseCase;
    private final NotificarClienteEmailUseCase notificarClienteEmailUseCase;

    @RabbitListener(queues = "${saga.rabbitmq.queue.cliente.notificar-falha-autocadastro.command}")
    public void handle(NotificarFalhaAutocadastroCommand command) {
        log.info("Recebido comando para notificar falha de autocadastro do CPF {}", command.cpf());

        try {
            var cliente = getClienteByCpfUseCase.executeAndReturnEntity(command.cpf());
            notificarClienteEmailUseCase.notificarFalhaAutocadastro(cliente, command.motivo());
        } catch (Exception ex) {
            log.error(
                "Falha ao notificar cliente {} sobre erro no autocadastro: {}",
                command.cpf(),
                ex.getMessage()
            );
        }
    }
}
