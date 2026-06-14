package br.ufpr.bantads.saga.sagas.remocaogerente;

import static br.ufpr.bantads.saga.sagas.remocaogerente.RemocaoGerenteStep.LISTAR_GERENTES_ATIVOS;
import static br.ufpr.bantads.saga.sagas.remocaogerente.RemocaoGerenteStep.REATRIBUIR_CONTAS;
import static br.ufpr.bantads.saga.sagas.remocaogerente.RemocaoGerenteStep.REMOVER_GERENTE;
import static br.ufpr.bantads.saga.sagas.remocaogerente.RemocaoGerenteStep.REVERTER_REATRIBUICAO_CONTAS_COMPENSACAO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.ListarGerentesAtivosCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.ReatribuirContasGerenteCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.RemoverGerenteCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ContasReatribuidasEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.GerenteRemovidoEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.GerentesAtivosListadosEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ListagemGerentesAtivosFalhouEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ReatribuicaoContasFalhouEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.RemocaoGerenteFalhouEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.response.RemocaoGerenteResponse;
import br.ufpr.bantads.saga.shared.SagaResponseRegistry;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaResult;
import br.ufpr.bantads.saga.shared.enums.SagaStatus;
import br.ufpr.bantads.saga.shared.service.SagaPersistenceService;

// Compensação saga remoção de gerente
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.ReverterReatribuicaoContasCompensacaoCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ReatribuicaoContasRevertidaCompensacaoEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ReversaoReatribuicaoContasCompensacaoFalhouEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RemocaoGerenteOrchestrator {

    public static final String MOTIVO_ULTIMO_GERENTE = "ULTIMO_GERENTE";

    private static final String SAGA_TYPE = "REMOCAO_GERENTE";

    private final RemocaoGerenteCommandPublisher commandPublisher;
    private final SagaResponseRegistry responseRegistry;
    private final SagaPersistenceService sagaPersistenceService;

    @Value("${saga.step.timeout-ms:10000}")
    private Long timeoutMs;

    public SagaResult iniciar(String cpf) {
        String sagaId = UUID.randomUUID().toString();
        CompletableFuture<SagaResult> future = responseRegistry.register(sagaId);

        sagaPersistenceService.createSaga(sagaId, SAGA_TYPE);

        ListarGerentesAtivosCommand command = new ListarGerentesAtivosCommand(sagaId, cpf);
        sagaPersistenceService.markStepSent(
            sagaId,
            LISTAR_GERENTES_ATIVOS.order(),
            LISTAR_GERENTES_ATIVOS.stepName(),
            ListarGerentesAtivosCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );
        commandPublisher.publishListarGerentesAtivos(command);

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("SAGA remoção de gerente {} falhou no aguardo", sagaId, ex);
            responseRegistry.cancel(sagaId);
            sagaPersistenceService.failSaga(sagaId, "SAGA não concluída: " + ex.getMessage());
            return new SagaErrorResponse(sagaId, "FAILED", "SAGA não concluída: " + ex.getMessage());
        }
    }

    public void handleGerentesAtivosListados(GerentesAtivosListadosEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} recebeu gerentes ativos: {}", sagaId, event.getCpfs());

        sagaPersistenceService.markStepCompleted(
            sagaId,
            LISTAR_GERENTES_ATIVOS.stepName(),
            GerentesAtivosListadosEvent.class.getSimpleName(),
            event
        );

        ListarGerentesAtivosCommand listarCommand;
        try {
            listarCommand = sagaPersistenceService.getCompletedStepPayload(
                sagaId, LISTAR_GERENTES_ATIVOS.stepName(), ListarGerentesAtivosCommand.class
            );
        } catch (Exception ex) {
            fail(sagaId, LISTAR_GERENTES_ATIVOS, "Estado do cpf removendo perdido entre steps");
            return;
        }

        ReatribuirContasGerenteCommand command = ReatribuirContasGerenteCommand.fromGerentesAtivos(
            listarCommand.getCpfRemovendo(), event
        );
        sagaPersistenceService.markStepSent(
            sagaId,
            REATRIBUIR_CONTAS.order(),
            REATRIBUIR_CONTAS.stepName(),
            ReatribuirContasGerenteCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );
        commandPublisher.publishReatribuirContasGerente(command);
    }

    public void handleContasReatribuidas(ContasReatribuidasEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} recebeu contas reatribuídas: {} para {}",
            sagaId, event.getContasReatribuidas(), event.getGerenteDestinoCpf());

        sagaPersistenceService.markStepCompleted(
            sagaId,
            REATRIBUIR_CONTAS.stepName(),
            ContasReatribuidasEvent.class.getSimpleName(),
            event
        );

        ListarGerentesAtivosCommand listarCommand;
        try {
            listarCommand = sagaPersistenceService.getCompletedStepPayload(
                sagaId, LISTAR_GERENTES_ATIVOS.stepName(), ListarGerentesAtivosCommand.class
            );
        } catch (Exception ex) {
            fail(sagaId, REATRIBUIR_CONTAS, "Estado do cpf removendo perdido antes da remoção");
            return;
        }

        RemoverGerenteCommand command = new RemoverGerenteCommand(sagaId, listarCommand.getCpfRemovendo());
        sagaPersistenceService.markStepSent(
            sagaId,
            REMOVER_GERENTE.order(),
            REMOVER_GERENTE.stepName(),
            RemoverGerenteCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );
        commandPublisher.publishRemoverGerente(command);
    }

    public void handleGerenteRemovido(GerenteRemovidoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} concluída — gerente removido: {}", sagaId, event.getCpf());

        sagaPersistenceService.markStepCompleted(
            sagaId,
            REMOVER_GERENTE.stepName(),
            GerenteRemovidoEvent.class.getSimpleName(),
            event
        );

        ContasReatribuidasEvent contasEvent;
        try {
            contasEvent = sagaPersistenceService.getCompletedStepResponse(
                sagaId, REATRIBUIR_CONTAS.stepName(), ContasReatribuidasEvent.class
            );
        } catch (Exception ex) {
            fail(sagaId, REMOVER_GERENTE, "Estado das contas reatribuídas perdido no último step");
            return;
        }

        sagaPersistenceService.completeSaga(sagaId);
        responseRegistry.complete(sagaId, RemocaoGerenteResponse.fromContasReatribuidas(event.getCpf(), contasEvent));
    }

    public void handleListagemGerentesAtivosFalhou(ListagemGerentesAtivosFalhouEvent event) {
        fail(event.getSagaId(), LISTAR_GERENTES_ATIVOS, event.getMotivo());
    }

    public void handleReatribuicaoContasFalhou(ReatribuicaoContasFalhouEvent event) {
        fail(event.getSagaId(), REATRIBUIR_CONTAS, "Falha ao reatribuir contas: " + event.getMotivo());
    }

    public void handleRemocaoGerenteFalhou(RemocaoGerenteFalhouEvent event) {
        String sagaId = event.getSagaId();
        String motivoOriginal = "Falha ao remover gerente: " + event.getMotivo();

        sagaPersistenceService.failStep(sagaId, REMOVER_GERENTE.stepName(), motivoOriginal);
        compensarReatribuicaoContas(sagaId, motivoOriginal);
    }

    public void handleReatribuicaoContasRevertidaCompensacao(ReatribuicaoContasRevertidaCompensacaoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} compensação concluída — contas revertidas: {}", sagaId, event.getContasRevertidas());

        sagaPersistenceService.markStepCompensated(
            sagaId,
            REVERTER_REATRIBUICAO_CONTAS_COMPENSACAO.stepName(),
            ReatribuicaoContasRevertidaCompensacaoEvent.class.getSimpleName(),
            event
        );
        sagaPersistenceService.completeCompensation(sagaId);

        String motivo = sagaPersistenceService.getSagaErrorMessage(sagaId);
        String mensagem = motivo == null || motivo.isBlank()
            ? "SAGA falhou; reatribuição de contas foi compensada"
            : motivo;
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", mensagem));
    }

    public void handleReversaoReatribuicaoContasCompensacaoFalhou(ReversaoReatribuicaoContasCompensacaoFalhouEvent event) {
        String sagaId = event.getSagaId();
        String motivo = sagaPersistenceService.getSagaErrorMessage(sagaId);
        String motivoCompensacao = (motivo == null || motivo.isBlank() ? "SAGA falhou" : motivo)
            + " | compensação de contas também falhou: " + event.getMotivo();

        log.error("SAGA {} compensação reatribuição falhou: {}", sagaId, event.getMotivo());
        failPersistedStep(sagaId, REVERTER_REATRIBUICAO_CONTAS_COMPENSACAO.stepName(), motivoCompensacao);
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivoCompensacao));
    }

    private void compensarReatribuicaoContas(String sagaId, String motivo) {
        ListarGerentesAtivosCommand listarCommand;
        ContasReatribuidasEvent contasEvent;
        try {
            listarCommand = sagaPersistenceService.getCompletedStepPayload(
                sagaId, LISTAR_GERENTES_ATIVOS.stepName(), ListarGerentesAtivosCommand.class
            );
            contasEvent = sagaPersistenceService.getCompletedStepResponse(
                sagaId, REATRIBUIR_CONTAS.stepName(), ContasReatribuidasEvent.class
            );
        } catch (Exception ex) {
            String motivoCompensacao = motivo + ". Não foi possível localizar estado da reatribuição para compensação";
            log.warn("SAGA remoção gerente {} não conseguiu iniciar compensação", sagaId, ex);
            sagaPersistenceService.failSaga(sagaId, motivoCompensacao);
            responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivoCompensacao));
            return;
        }

        ReverterReatribuicaoContasCompensacaoCommand command = new ReverterReatribuicaoContasCompensacaoCommand(
            sagaId,
            listarCommand.getCpfRemovendo(),
            contasEvent.getGerenteDestinoCpf(),
            contasEvent.getNumerosContasMovidas()
        );

        sagaPersistenceService.requireCompensation(sagaId, motivo);
        sagaPersistenceService.markStepSent(
            sagaId,
            REVERTER_REATRIBUICAO_CONTAS_COMPENSACAO.order(),
            REVERTER_REATRIBUICAO_CONTAS_COMPENSACAO.stepName(),
            ReverterReatribuicaoContasCompensacaoCommand.class.getSimpleName(),
            command,
            SagaStatus.COMPENSATION_REQUIRED
        );
        commandPublisher.publishReverterReatribuicaoContasCompensacao(command);
    }

    private void fail(String sagaId, RemocaoGerenteStep step, String motivo) {
        log.warn("SAGA remoção gerente {} falhou: {}", sagaId, motivo);
        failPersistedStep(sagaId, step.stepName(), motivo);
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivo));
    }

    private void failPersistedStep(String sagaId, String stepName, String motivo) {
        sagaPersistenceService.failStep(sagaId, stepName, motivo);
        sagaPersistenceService.failSaga(sagaId, motivo);
    }
}