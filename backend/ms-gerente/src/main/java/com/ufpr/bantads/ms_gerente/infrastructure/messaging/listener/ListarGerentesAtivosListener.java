package com.ufpr.bantads.ms_gerente.infrastructure.messaging.listener;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.ms_gerente.application.dto.command.ListarGerentesAtivosCommand;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerentesAtivosListadosEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.ListagemGerentesAtivosFalhouEvent;
import com.ufpr.bantads.ms_gerente.domain.model.entity.Gerente;
import com.ufpr.bantads.ms_gerente.domain.repository.GerenteRepository;
import com.ufpr.bantads.ms_gerente.infrastructure.messaging.publisher.RemocaoGerenteEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListarGerentesAtivosListener {

    private static final String MOTIVO_ULTIMO_GERENTE = "ULTIMO_GERENTE";

    private final GerenteRepository gerenteRepository;
    private final RemocaoGerenteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.gerente.listar-ativos.command}")
    public void handle(ListarGerentesAtivosCommand command) {
        log.info("Recebido ListarGerentesAtivosCommand saga {} cpfRemovendo {}", command.sagaId(), command.cpfRemovendo());

        try {
            List<String> cpfs = gerenteRepository.findAll().stream()
                .map(Gerente::getCpf)
                .filter(cpf -> !cpf.equals(command.cpfRemovendo()))
                .toList();

            if (cpfs.isEmpty()) {
                publisher.publishListagemGerentesAtivosFalhou(
                    new ListagemGerentesAtivosFalhouEvent(command.sagaId(), MOTIVO_ULTIMO_GERENTE)
                );
                return;
            }

            publisher.publishGerentesAtivosListados(new GerentesAtivosListadosEvent(command.sagaId(), cpfs));
        } catch (Exception ex) {
            log.error("Falha ao listar gerentes ativos saga {}", command.sagaId(), ex);
            publisher.publishListagemGerentesAtivosFalhou(
                new ListagemGerentesAtivosFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}