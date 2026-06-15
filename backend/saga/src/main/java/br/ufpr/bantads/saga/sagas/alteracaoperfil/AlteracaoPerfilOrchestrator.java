package br.ufpr.bantads.saga.sagas.alteracaoperfil;

import static br.ufpr.bantads.saga.sagas.alteracaoperfil.AlteracaoPerfilStep.ALTERAR_LIMITE_CONTA;
import static br.ufpr.bantads.saga.sagas.alteracaoperfil.AlteracaoPerfilStep.ALTERAR_PERFIL_CLIENTE;
import static br.ufpr.bantads.saga.sagas.alteracaoperfil.AlteracaoPerfilStep.ALTERAR_USUARIO_AUTH;
import static br.ufpr.bantads.saga.sagas.alteracaoperfil.AlteracaoPerfilStep.REVERTER_PERFIL_CLIENTE_COMPENSACAO;
import static br.ufpr.bantads.saga.sagas.alteracaoperfil.AlteracaoPerfilStep.REVERTER_USUARIO_AUTH_COMPENSACAO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarLimiteContaCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarUsuarioClienteAuthCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.ReverterAlteracaoPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.ReverterAlteracaoUsuarioClienteAuthCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.AlteracaoUsuarioClienteAuthFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteAlteracaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilRevertidoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteReversaoPerfilFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ContaLimiteAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ReversaoUsuarioClienteAuthFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.UsuarioClienteAuthAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.UsuarioClienteAuthRevertidoEvent;
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
            ALTERAR_PERFIL_CLIENTE.order(),
            ALTERAR_PERFIL_CLIENTE.stepName(),
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
            ALTERAR_PERFIL_CLIENTE.stepName(),
            ClientePerfilAlteradoEvent.class.getSimpleName(),
            event
        );

        AlterarUsuarioClienteAuthCommand command = AlterarUsuarioClienteAuthCommand.fromEvent(event);

        sagaPersistenceService.markStepSent(
            event.getSagaId(),
            ALTERAR_USUARIO_AUTH.order(),
            ALTERAR_USUARIO_AUTH.stepName(),
            AlterarUsuarioClienteAuthCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );

        commandPublisher.publishAlterarUsuarioClienteAuth(command);
    }

    public void handleUsuarioClienteAuthAlterado(UsuarioClienteAuthAlteradoEvent event) {
        log.info("SAGA {} recebeu sucesso do MS Auth", event.sagaId());

        sagaPersistenceService.markStepCompleted(
            event.sagaId(),
            ALTERAR_USUARIO_AUTH.stepName(),
            UsuarioClienteAuthAlteradoEvent.class.getSimpleName(),
            event
        );

        ClientePerfilAlteradoEvent clienteEvent =
            sagaPersistenceService.getCompletedStepResponse(
                event.sagaId(),
                ALTERAR_PERFIL_CLIENTE.stepName(),
                ClientePerfilAlteradoEvent.class
            );

        AlterarLimiteContaCommand command = AlterarLimiteContaCommand.fromEvent(clienteEvent);

        sagaPersistenceService.markStepSent(
            event.sagaId(),
            ALTERAR_LIMITE_CONTA.order(),
            ALTERAR_LIMITE_CONTA.stepName(),
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
            ALTERAR_LIMITE_CONTA.stepName(),
            ContaLimiteAlteradoEvent.class.getSimpleName(),
            event
        );

        ClientePerfilAlteradoEvent clienteEvent =
            sagaPersistenceService.getCompletedStepResponse(
                event.getSagaId(),
                ALTERAR_PERFIL_CLIENTE.stepName(),
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
            ALTERAR_PERFIL_CLIENTE.stepName(),
            event.motivo()
        );

        fail(event.sagaId(), event.cpf(), "Falha no MS Cliente: " + event.motivo());
    }

    public void handleAlteracaoUsuarioClienteAuthFalhou(AlteracaoUsuarioClienteAuthFalhouEvent event) {
        String motivo = "Falha no MS Auth: " + event.motivo();

        sagaPersistenceService.failStep(
            event.sagaId(),
            ALTERAR_USUARIO_AUTH.stepName(),
            event.motivo()
        );

        sagaPersistenceService.requireCompensation(event.sagaId(), motivo);
        iniciarCompensacaoCliente(event.sagaId(), event.cpf(), motivo);
    }

    public void handleContaAlteracaoLimiteFalhou(ClienteAlteracaoFalhouEvent event) {
        String motivo = "Falha no MS Conta: " + event.motivo();

        sagaPersistenceService.failStep(
            event.sagaId(),
            ALTERAR_LIMITE_CONTA.stepName(),
            event.motivo()
        );

        sagaPersistenceService.requireCompensation(
            event.sagaId(),
            motivo
        );

        iniciarCompensacaoAuth(event.sagaId(), event.cpf(), motivo);
    }

    public void handleUsuarioClienteAuthRevertido(UsuarioClienteAuthRevertidoEvent event) {
        log.info("SAGA {} recebeu compensação do MS Auth", event.sagaId());

        sagaPersistenceService.markStepCompensated(
            event.sagaId(),
            REVERTER_USUARIO_AUTH_COMPENSACAO.stepName(),
            UsuarioClienteAuthRevertidoEvent.class.getSimpleName(),
            event
        );

        String motivo = sagaPersistenceService.getSagaErrorMessage(event.sagaId());
        iniciarCompensacaoCliente(event.sagaId(), event.cpf(), motivo);
    }

    public void handleReversaoUsuarioClienteAuthFalhou(ReversaoUsuarioClienteAuthFalhouEvent event) {
        String motivoOriginal = sagaPersistenceService.getSagaErrorMessage(event.sagaId());
        String motivo = (motivoOriginal == null || motivoOriginal.isBlank())
            ? "Falha ao compensar usuário cliente no MS Auth: " + event.motivo()
            : motivoOriginal + ". Falha ao compensar usuário cliente no MS Auth: " + event.motivo();

        sagaPersistenceService.failStep(
            event.sagaId(),
            REVERTER_USUARIO_AUTH_COMPENSACAO.stepName(),
            event.motivo()
        );

        fail(event.sagaId(), event.cpf(), motivo);
    }

    private void iniciarCompensacaoAuth(String sagaId, String cpf, String motivo) {
        try {
            UsuarioClienteAuthAlteradoEvent authEvent =
                sagaPersistenceService.getCompletedStepResponse(
                    sagaId,
                    ALTERAR_USUARIO_AUTH.stepName(),
                    UsuarioClienteAuthAlteradoEvent.class
                );

            ReverterAlteracaoUsuarioClienteAuthCommand command =
                ReverterAlteracaoUsuarioClienteAuthCommand.fromEvent(authEvent);

            sagaPersistenceService.markStepSent(
                sagaId,
                REVERTER_USUARIO_AUTH_COMPENSACAO.order(),
                REVERTER_USUARIO_AUTH_COMPENSACAO.stepName(),
                ReverterAlteracaoUsuarioClienteAuthCommand.class.getSimpleName(),
                command,
                SagaStatus.COMPENSATION_REQUIRED
            );

            commandPublisher.publishReverterAlteracaoUsuarioClienteAuth(command);
        } catch (Exception ex) {
            fail(
                sagaId,
                cpf,
                motivo + ". Não foi possível iniciar compensação: " + ex.getMessage()
            );
        }
    }

    public void handleClientePerfilRevertido(ClientePerfilRevertidoEvent event) {
        log.info("SAGA {} recebeu compensação do MS Cliente", event.sagaId());

        sagaPersistenceService.markStepCompensated(
            event.sagaId(),
            REVERTER_PERFIL_CLIENTE_COMPENSACAO.stepName(),
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
            REVERTER_PERFIL_CLIENTE_COMPENSACAO.stepName(),
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

    private void iniciarCompensacaoCliente(String sagaId, String cpf, String motivo) {
        try {
            ClientePerfilAlteradoEvent clienteEvent =
                sagaPersistenceService.getCompletedStepResponse(
                    sagaId,
                    ALTERAR_PERFIL_CLIENTE.stepName(),
                    ClientePerfilAlteradoEvent.class
                );

            ReverterAlteracaoPerfilClienteCommand command =
                ReverterAlteracaoPerfilClienteCommand.fromEvent(clienteEvent);

            sagaPersistenceService.markStepSent(
                sagaId,
                REVERTER_PERFIL_CLIENTE_COMPENSACAO.order(),
                REVERTER_PERFIL_CLIENTE_COMPENSACAO.stepName(),
                ReverterAlteracaoPerfilClienteCommand.class.getSimpleName(),
                command,
                SagaStatus.COMPENSATION_REQUIRED
            );

            commandPublisher.publishReverterAlteracaoPerfil(command);
        } catch (Exception ex) {
            fail(
                sagaId,
                cpf,
                motivo + ". Não foi possível iniciar compensação: " + ex.getMessage()
            );
        }
    }

}
