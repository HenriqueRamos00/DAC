package br.ufpr.bantads.saga.sagas.aprovacaocliente;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.AprovacaoClienteFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ClienteAprovadoEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ContaCriadaSagaEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.CriacaoContaFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.CriacaoUsuarioClienteFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.GerenteParaNovaContaSelecionadoEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.GerentesAtivosListadosEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ListagemGerentesAtivosFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.SelecaoGerenteParaNovaContaFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.UsuarioClienteCriadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = "${saga.rabbitmq.queue.aprovacao-cliente.response}")
public class AprovacaoClienteResponseListener {

    private final AprovacaoClienteOrchestrator orchestrator;

    @RabbitHandler
    public void handleUsuarioClienteCriado(UsuarioClienteCriadoEvent event) {
        orchestrator.handleUsuarioClienteCriado(event);
    }

    @RabbitHandler
    public void handleCriacaoUsuarioClienteFalhou(CriacaoUsuarioClienteFalhouEvent event) {
        orchestrator.handleCriacaoUsuarioClienteFalhou(event);
    }

    @RabbitHandler
    public void handleGerentesAtivosListados(GerentesAtivosListadosEvent event) {
        orchestrator.handleGerentesAtivosListados(event);
    }

    @RabbitHandler
    public void handleListagemGerentesAtivosFalhou(ListagemGerentesAtivosFalhouEvent event) {
        orchestrator.handleListagemGerentesAtivosFalhou(event);
    }

    @RabbitHandler
    public void handleGerenteParaNovaContaSelecionado(GerenteParaNovaContaSelecionadoEvent event) {
        orchestrator.handleGerenteParaNovaContaSelecionado(event);
    }

    @RabbitHandler
    public void handleSelecaoGerenteParaNovaContaFalhou(SelecaoGerenteParaNovaContaFalhouEvent event) {
        orchestrator.handleSelecaoGerenteParaNovaContaFalhou(event);
    }

    @RabbitHandler
    public void handleContaCriada(ContaCriadaSagaEvent event) {
        orchestrator.handleContaCriada(event);
    }

    @RabbitHandler
    public void handleCriacaoContaFalhou(CriacaoContaFalhouEvent event) {
        orchestrator.handleCriacaoContaFalhou(event);
    }

    @RabbitHandler
    public void handleClienteAprovado(ClienteAprovadoEvent event) {
        orchestrator.handleClienteAprovado(event);
    }

    @RabbitHandler
    public void handleAprovacaoClienteFalhou(AprovacaoClienteFalhouEvent event) {
        orchestrator.handleAprovacaoClienteFalhou(event);
    }
}
