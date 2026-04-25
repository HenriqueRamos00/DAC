package com.ufpr.bantads.cliente.infrastructure.messaging.listener;

import com.ufpr.bantads.cliente.application.dto.command.AprovarClienteCommand;
import com.ufpr.bantads.cliente.application.dto.event.AprovacaoClienteFalhouEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClienteAprovadoEvent;
import com.ufpr.bantads.cliente.application.usecase.AprovarClienteUseCase;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoEncontradoException;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoPendenteException;
import com.ufpr.bantads.cliente.infrastructure.messaging.publisher.AprovarClienteEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AprovarClienteListener {

    private final AprovarClienteUseCase aprovarClienteUseCase;
    private final AprovarClienteEventPublisher aprovarClienteEventPublisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.cliente.aprovar.command}")
    public void handle(AprovarClienteCommand command) {
        log.info("Recebido comando para aprovar cliente com CPF {}", command.cpf());

        try {
            var cliente = aprovarClienteUseCase.executeAndReturnEntity(command.cpf());
            aprovarClienteEventPublisher.publishSucesso(ClienteAprovadoEvent.fromEntity(cliente));
        } catch (ClienteNaoEncontradoException | ClienteNaoPendenteException ex) {
            log.warn("Nao foi possivel aprovar cliente {}: {}", command.cpf(), ex.getMessage());
            aprovarClienteEventPublisher.publishFalha(
                new AprovacaoClienteFalhouEvent(command.cpf(), ex.getMessage())
            );
        }
    }
}
