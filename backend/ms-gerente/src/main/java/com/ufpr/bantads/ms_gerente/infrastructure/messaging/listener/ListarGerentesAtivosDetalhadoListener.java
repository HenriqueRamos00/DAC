package com.ufpr.bantads.ms_gerente.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.ms_gerente.application.dto.command.ListarGerentesAtivosDetalhadoCommand;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteAtivoDetalhado;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerentesAtivosDetalhadosListadosEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.ListagemGerentesAtivosDetalhadosFalhouEvent;
import com.ufpr.bantads.ms_gerente.application.usecase.ListAllGerentesUseCase;
import com.ufpr.bantads.ms_gerente.infrastructure.messaging.publisher.ListarGerentesAtivosDetalhadoEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListarGerentesAtivosDetalhadoListener {

    private final ListAllGerentesUseCase listAllGerentesUseCase;
    private final ListarGerentesAtivosDetalhadoEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.gerente.listar-ativos-detalhado.command}")
    public void handle(ListarGerentesAtivosDetalhadoCommand command) {
        log.info("Recebido ListarGerentesAtivosDetalhadoCommand saga {}", command.sagaId());

        try {
            var gerentes = listAllGerentesUseCase.execute()
                .stream()
                .map(GerenteAtivoDetalhado::fromResponse)
                .toList();

            publisher.publishGerentesAtivosListados(
                new GerentesAtivosDetalhadosListadosEvent(command.sagaId(), gerentes)
            );
        } catch (Exception ex) {
            log.error("Falha ao listar gerentes ativos saga {}", command.sagaId(), ex);
            publisher.publishListagemFalhou(
                new ListagemGerentesAtivosDetalhadosFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}
