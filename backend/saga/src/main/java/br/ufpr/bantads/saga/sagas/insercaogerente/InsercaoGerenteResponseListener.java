package br.ufpr.bantads.saga.sagas.insercaogerente;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.AtribuicaoGerenteContaFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.ConsultaGerenteMaisContasFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.CriacaoUsuarioGerenteFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteAtribuidoContaEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteInseridoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteMaisContasConsultadoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.InsercaoGerenteFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.UsuarioGerenteCriadoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteOrchestrator;

// Compensação saga inserir gerente
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.ExclusaoUsuarioGerenteCompensacaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteRemovidoCompensacaoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.RemocaoGerenteCompensacaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.UsuarioGerenteExcluidoCompensacaoEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = "${saga.rabbitmq.queue.inserir-gerente.response}")
public class InsercaoGerenteResponseListener {

    private final InsercaoGerenteOrchestrator orchestrator;

    @RabbitHandler
    public void handleGerenteMaisContasConsultado(GerenteMaisContasConsultadoEvent event) {
        log.info("Recebido GerenteMaisContasConsultadoEvent saga {}", event.getSagaId());
        orchestrator.handleGerenteMaisContasConsultado(event);
    }

    @RabbitHandler
    public void handleConsultaGerenteMaisContasFalhou(ConsultaGerenteMaisContasFalhouEvent event) {
        log.info("Recebido ConsultaGerenteMaisContasFalhouEvent saga {}", event.getSagaId());
        orchestrator.handleConsultaGerenteMaisContasFalhou(event);
    }

    @RabbitHandler
    public void handleGerenteInserido(GerenteInseridoEvent event) {
        log.info("Recebido GerenteInseridoEvent saga {}", event.getSagaId());
        orchestrator.handleGerenteInserido(event);
    }

    @RabbitHandler
    public void handleInsercaoGerenteFalhou(InsercaoGerenteFalhouEvent event) {
        log.info("Recebido InsercaoGerenteFalhouEvent saga {}", event.getSagaId());
        orchestrator.handleInsercaoGerenteFalhou(event);
    }

    @RabbitHandler
    public void handleGerenteAtribuidoConta(GerenteAtribuidoContaEvent event) {
        log.info("Recebido GerenteAtribuidoContaEvent saga {}", event.getSagaId());
        orchestrator.handleGerenteAtribuidoConta(event);
    }

    @RabbitHandler
    public void handleAtribuicaoGerenteContaFalhou(AtribuicaoGerenteContaFalhouEvent event) {
        log.info("Recebido AtribuicaoGerenteContaFalhouEvent saga {}", event.getSagaId());
        orchestrator.handleAtribuicaoGerenteContaFalhou(event);
    }

    @RabbitHandler
    public void handleUsuarioGerenteCriado(UsuarioGerenteCriadoEvent event) {
        log.info("Recebido UsuarioGerenteCriadoEvent saga {}", event.getSagaId());
        orchestrator.handleUsuarioGerenteCriado(event);
    }

    @RabbitHandler
    public void handleCriacaoUsuarioGerenteFalhou(CriacaoUsuarioGerenteFalhouEvent event) {
        log.info("Recebido CriacaoUsuarioGerenteFalhouEvent saga {}", event.getSagaId());
        orchestrator.handleCriacaoUsuarioGerenteFalhou(event);
    }

    @RabbitHandler
    public void handleGerenteRemovidoCompensacao(GerenteRemovidoCompensacaoEvent event) {
        log.info("Recebido GerenteRemovidoCompensacaoEvent saga {}", event.getSagaId());
        orchestrator.handleGerenteRemovidoCompensacao(event);
    }

    @RabbitHandler
    public void handleRemocaoGerenteCompensacaoFalhou(RemocaoGerenteCompensacaoFalhouEvent event) {
        log.info("Recebido RemocaoGerenteCompensacaoFalhouEvent saga {}", event.getSagaId());
        orchestrator.handleRemocaoGerenteCompensacaoFalhou(event);
    }

    @RabbitHandler
    public void handleUsuarioGerenteExcluidoCompensacao(UsuarioGerenteExcluidoCompensacaoEvent event) {
        log.info("Recebido UsuarioGerenteExcluidoCompensacaoEvent saga {}", event.getSagaId());
        orchestrator.handleUsuarioGerenteExcluidoCompensacao(event);
    }

    @RabbitHandler
    public void handleExclusaoUsuarioGerenteCompensacaoFalhou(ExclusaoUsuarioGerenteCompensacaoFalhouEvent event) {
        log.info("Recebido ExclusaoUsuarioGerenteCompensacaoFalhouEvent saga {}", event.getSagaId());
        orchestrator.handleExclusaoUsuarioGerenteCompensacaoFalhou(event);
    }
}