package br.ufpr.bantads.saga.sagas.remocaogerente;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ContasReatribuidasEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.GerenteRemovidoEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.GerentesAtivosListadosEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ListagemGerentesAtivosFalhouEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ReatribuicaoContasFalhouEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.RemocaoGerenteFalhouEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = "${saga.rabbitmq.queue.remocao-gerente.response}")
public class RemocaoGerenteResponseListener {

    private final RemocaoGerenteOrchestrator orchestrator;

    @RabbitHandler
    public void handleGerentesAtivosListados(GerentesAtivosListadosEvent event) {
        log.info("Recebido GerentesAtivosListadosEvent saga {}", event.getSagaId());
        orchestrator.handleGerentesAtivosListados(event);
    }

    @RabbitHandler
    public void handleListagemGerentesAtivosFalhou(ListagemGerentesAtivosFalhouEvent event) {
        log.info("Recebido ListagemGerentesAtivosFalhouEvent saga {}", event.getSagaId());
        orchestrator.handleListagemGerentesAtivosFalhou(event);
    }

    @RabbitHandler
    public void handleContasReatribuidas(ContasReatribuidasEvent event) {
        log.info("Recebido ContasReatribuidasEvent saga {}", event.getSagaId());
        orchestrator.handleContasReatribuidas(event);
    }

    @RabbitHandler
    public void handleReatribuicaoContasFalhou(ReatribuicaoContasFalhouEvent event) {
        log.info("Recebido ReatribuicaoContasFalhouEvent saga {}", event.getSagaId());
        orchestrator.handleReatribuicaoContasFalhou(event);
    }

    @RabbitHandler
    public void handleGerenteRemovido(GerenteRemovidoEvent event) {
        log.info("Recebido GerenteRemovidoEvent saga {}", event.getSagaId());
        orchestrator.handleGerenteRemovido(event);
    }

    @RabbitHandler
    public void handleRemocaoGerenteFalhou(RemocaoGerenteFalhouEvent event) {
        log.info("Recebido RemocaoGerenteFalhouEvent saga {}", event.getSagaId());
        orchestrator.handleRemocaoGerenteFalhou(event);
    }
}