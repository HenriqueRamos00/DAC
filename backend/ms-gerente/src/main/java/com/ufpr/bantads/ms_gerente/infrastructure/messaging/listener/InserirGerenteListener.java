package com.ufpr.bantads.ms_gerente.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.ms_gerente.application.dto.command.InserirGerenteCommand;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteInseridoEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.InsercaoGerenteFalhouEvent;
import com.ufpr.bantads.ms_gerente.application.dto.request.GerenteRequest;
import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;
import com.ufpr.bantads.ms_gerente.application.usecase.CreateGerenteUseCase;
import com.ufpr.bantads.ms_gerente.domain.exception.GerenteJaExisteException;
import com.ufpr.bantads.ms_gerente.infrastructure.messaging.publisher.InsercaoGerenteEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InserirGerenteListener {

    private static final String MOTIVO_CPF_DUPLICADO = "CPF_DUPLICADO";

    private final CreateGerenteUseCase createGerenteUseCase;
    private final InsercaoGerenteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.gerente.inserir.command}")
    public void handle(InserirGerenteCommand command) {
        log.info("Recebido InserirGerenteCommand saga {} cpf {}", command.sagaId(), command.cpf());

        try {
            GerenteResponse response = createGerenteUseCase.execute(GerenteRequest.fromCommand(command));
            publisher.publishGerenteInserido(GerenteInseridoEvent.fromResponse(command.sagaId(), response));
        } catch (GerenteJaExisteException ex) {
            log.warn("Gerente já existe saga {}: {}", command.sagaId(), ex.getMessage());
            publisher.publishInsercaoGerenteFalhou(
                new InsercaoGerenteFalhouEvent(command.sagaId(), MOTIVO_CPF_DUPLICADO)
            );
        } catch (Exception ex) {
            log.error("Falha ao inserir gerente saga {}", command.sagaId(), ex);
            publisher.publishInsercaoGerenteFalhou(
                new InsercaoGerenteFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}
