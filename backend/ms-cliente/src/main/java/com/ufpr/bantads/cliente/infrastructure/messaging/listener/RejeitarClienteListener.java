package com.ufpr.bantads.cliente.infrastructure.messaging.listener;

import com.ufpr.bantads.cliente.application.dto.command.RejeitarClienteCommand;
import com.ufpr.bantads.cliente.application.dto.event.ClienteRejeitadoEvent;
import com.ufpr.bantads.cliente.application.dto.event.RejeicaoClienteFalhouEvent;
import com.ufpr.bantads.cliente.application.usecase.NotificarClienteEmailUseCase;
import com.ufpr.bantads.cliente.application.usecase.RejeitarClienteUseCase;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoEncontradoException;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoPendenteException;
import com.ufpr.bantads.cliente.infrastructure.messaging.publisher.RejeitarClienteEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RejeitarClienteListener {

    private final RejeitarClienteUseCase rejeitarClienteUseCase;
    private final RejeitarClienteEventPublisher rejeitarClienteEventPublisher;
    private final NotificarClienteEmailUseCase notificarClienteEmailUseCase;

    @RabbitListener(queues = "${saga.rabbitmq.queue.cliente.rejeitar.command}")
    public void handle(RejeitarClienteCommand command) {
        log.info("Recebido comando para rejeitar cliente com CPF {}", command.cpf());

        try {
            var cliente = rejeitarClienteUseCase.executeAndReturnEntity(command.cpf(), command.motivo());

            try {
                notificarClienteEmailUseCase.notificarRejeicao(cliente);
            } catch (Exception emailEx) {
                log.error("Falha ao enviar email de rejeição para {}: {}",
                    cliente.getEmail(), emailEx.getMessage());
            }

            rejeitarClienteEventPublisher.publishSucesso(ClienteRejeitadoEvent.fromEntity(cliente));
        } catch (ClienteNaoEncontradoException | ClienteNaoPendenteException ex) {
            log.warn("Não foi possível rejeitar cliente {}: {}", command.cpf(), ex.getMessage());
            rejeitarClienteEventPublisher.publishFalha(
                new RejeicaoClienteFalhouEvent(command.cpf(), ex.getMessage())
            );
        }
    }
}
