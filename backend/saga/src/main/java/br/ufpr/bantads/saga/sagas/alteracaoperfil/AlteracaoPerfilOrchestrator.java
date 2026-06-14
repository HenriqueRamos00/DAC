package br.ufpr.bantads.saga.sagas.alteracaoperfil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarLimiteContaCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.ReverterAlteracaoPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteAlteracaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilRevertidoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteReversaoPerfilFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ContaLimiteAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.request.AlterarPerfilRequest;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.response.AlterarPerfilSagaResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaResult;
import br.ufpr.bantads.saga.shared.enums.SagaStatus;
import br.ufpr.bantads.saga.shared.service.SagaPersistenceService;
import br.ufpr.bantads.saga.shared.SagaResponseRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlteracaoPerfilOrchestrator {

    private static final String SAGA_TYPE = "ALTERACAO_PERFIL_CLIENTE";

    private static final String STEP_ALTERAR_PERFIL_CLIENTE = "ALTERAR_PERFIL_CLIENTE";
    private static final String STEP_ALTERAR_LIMITE_CONTA = "ALTERAR_LIMITE_CONTA";
    private static final String STEP_REVERTER_PERFIL_CLIENTE = "REVERTER_PERFIL_CLIENTE";

    private final AlteracaoPerfilCommandPublisher commandPublisher;
    private final SagaResponseRegistry responseRegistry;
    private final SagaPersistenceService sagaPersistenceService;

    @Value("${saga.step.timeout-ms:10000}")
    private Long timeoutMs;

    public SagaResult iniciar(String cpf, AlterarPerfilRequest request) {
        String sagaId = UUID.randomUUID().toString();

        sagaPersistenceService.createSaga(sagaId, SAGA_TYPE);

        CompletableFuture<SagaResult> future = responseRegistry.register(sagaId);

        AlterarPerfilClienteCommand command =
            AlterarPerfilClienteCommand.fromRequest(cpf, sagaId, request);

        sagaPersistenceService.markStepSent(
            sagaId,
            1,
            STEP_ALTERAR_PERFIL_CLIENTE,
            AlterarPerfilClienteCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );

        commandPublisher.publishAlterarPerfil(command);

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("SAGA alteração de perfil {} falhou no aguardo", sagaId, ex);
            responseRegistry.cancel(sagaId);
            sagaPersistenceService.failSaga(
                sagaId,
                "SAGA de alteração de perfil não concluída: " + ex.getMessage()
            );

            return new SagaErrorResponse(sagaId, "FAILED",
                "SAGA não concluída: " + ex.getMessage());
        }
    }

    public void handleClientePerfilAlterado(ClientePerfilAlteradoEvent event) {
        log.info("SAGA {} recebeu sucesso do MS Cliente", event.getSagaId());

        sagaPersistenceService.markStepCompleted(
            event.getSagaId(),
            STEP_ALTERAR_PERFIL_CLIENTE,
            ClientePerfilAlteradoEvent.class.getSimpleName(),
            event
        );

        AlterarLimiteContaCommand command = AlterarLimiteContaCommand.fromEvent(event);

        sagaPersistenceService.markStepSent(
            event.getSagaId(),
            2,
            STEP_ALTERAR_LIMITE_CONTA,
            AlterarLimiteContaCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );

        commandPublisher.publishAlterarLimite(command);
    }

    public void handleContaLimiteAlterado(ContaLimiteAlteradoEvent event) {
        log.info("SAGA {} recebeu sucesso do MS Conta", event.getSagaId());

        sagaPersistenceService.markStepCompleted(
            event.getSagaId(),
            STEP_ALTERAR_LIMITE_CONTA,
            ContaLimiteAlteradoEvent.class.getSimpleName(),
            event
        );

        ClientePerfilAlteradoEvent clienteEvent =
            sagaPersistenceService.getCompletedStepResponse(
                event.getSagaId(),
                STEP_ALTERAR_PERFIL_CLIENTE,
                ClientePerfilAlteradoEvent.class
            );

        AlterarPerfilSagaResponse response =
            AlterarPerfilSagaResponse.fromEvent(clienteEvent, event);

        sagaPersistenceService.completeSaga(event.getSagaId());

        responseRegistry.complete(event.getSagaId(), response);
    }

    public void handleClienteAlteracaoPerfilFalhou(ClienteAlteracaoFalhouEvent event) {
        sagaPersistenceService.failStep(
            event.sagaId(),
            STEP_ALTERAR_PERFIL_CLIENTE,
            event.motivo()
        );

        fail(event.sagaId(), event.cpf(), "Falha no MS Cliente: " + event.motivo());
    }

    public void handleContaAlteracaoLimiteFalhou(ClienteAlteracaoFalhouEvent event) {
        String motivo = "Falha no MS Conta: " + event.motivo();

        sagaPersistenceService.failStep(
            event.sagaId(),
            STEP_ALTERAR_LIMITE_CONTA,
            event.motivo()
        );

        sagaPersistenceService.requireCompensation(
            event.sagaId(),
            motivo
        );

        try {
            ClientePerfilAlteradoEvent clienteEvent =
                sagaPersistenceService.getCompletedStepResponse(
                    event.sagaId(),
                    STEP_ALTERAR_PERFIL_CLIENTE,
                    ClientePerfilAlteradoEvent.class
                );

            ReverterAlteracaoPerfilClienteCommand command =
                ReverterAlteracaoPerfilClienteCommand.fromEvent(clienteEvent);

            sagaPersistenceService.markStepSent(
                event.sagaId(),
                3,
                STEP_REVERTER_PERFIL_CLIENTE,
                ReverterAlteracaoPerfilClienteCommand.class.getSimpleName(),
                command,
                SagaStatus.COMPENSATION_REQUIRED
            );

            commandPublisher.publishReverterAlteracaoPerfil(command);
        } catch (Exception ex) {
            fail(
                event.sagaId(),
                event.cpf(),
                motivo + ". Não foi possível iniciar compensação: " + ex.getMessage()
            );
        }
    }

    public void handleClientePerfilRevertido(ClientePerfilRevertidoEvent event) {
        log.info("SAGA {} recebeu compensação do MS Cliente", event.sagaId());

        sagaPersistenceService.markStepCompensated(
            event.sagaId(),
            STEP_REVERTER_PERFIL_CLIENTE,
            ClientePerfilRevertidoEvent.class.getSimpleName(),
            event
        );

        String motivoOriginal = sagaPersistenceService.getSagaErrorMessage(event.sagaId());
        String motivo = (motivoOriginal == null || motivoOriginal.isBlank())
            ? "SAGA de alteração de perfil compensada"
            : motivoOriginal + ". Perfil do cliente revertido no MS Cliente";

        sagaPersistenceService.completeCompensation(event.sagaId());

        responseRegistry.complete(
            event.sagaId(),
            new SagaErrorResponse(event.sagaId(), "COMPENSATED", motivo)
        );
    }

    public void handleClienteReversaoPerfilFalhou(ClienteReversaoPerfilFalhouEvent event) {
        String motivoOriginal = sagaPersistenceService.getSagaErrorMessage(event.sagaId());
        String motivo = (motivoOriginal == null || motivoOriginal.isBlank())
            ? "Falha ao compensar alteração de perfil no MS Cliente: " + event.motivo()
            : motivoOriginal + ". Falha ao compensar alteração de perfil no MS Cliente: " + event.motivo();

        sagaPersistenceService.failStep(
            event.sagaId(),
            STEP_REVERTER_PERFIL_CLIENTE,
            event.motivo()
        );

        fail(event.sagaId(), event.cpf(), motivo);
    }

    private void fail(String sagaId, String cpf, String motivo) {
        log.warn("SAGA {} falhou para CPF {}: {}", sagaId, cpf, motivo);

        sagaPersistenceService.failSaga(sagaId, motivo);

        responseRegistry.complete(
            sagaId,
            new SagaErrorResponse(sagaId, "FAILED", motivo)
        );
    }

}
