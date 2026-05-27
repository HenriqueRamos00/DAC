package br.ufpr.bantads.saga.sagas.alteracaoperfil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarLimiteContaCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteAlteracaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ContaLimiteAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.request.AlterarPerfilRequest;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.response.AlterarPerfilSagaResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
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

    private final AlteracaoPerfilCommandPublisher commandPublisher;
    private final SagaResponseRegistry responseRegistry;
    private final SagaPersistenceService sagaPersistenceService;

    private final Map<String, ClientePerfilAlteradoEvent> clienteAlteradoPorSaga = new ConcurrentHashMap<>();

    @Value("${saga.step.timeout-ms:10000}")
    private Long timeoutMs;

    public AlterarPerfilSagaResponse iniciar(String cpf, AlterarPerfilRequest request) {
        String sagaId = UUID.randomUUID().toString();

        sagaPersistenceService.createSaga(sagaId, SAGA_TYPE);

        CompletableFuture<Object> future = responseRegistry.register(sagaId);

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
            Object result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            if (result instanceof AlterarPerfilSagaResponse response) {
                return response;
            }
            if (result instanceof SagaErrorResponse error) {
                //Implementar Error handler
                throw new IllegalStateException(error.motivo());
            }
            throw new IllegalStateException("Resposta inesperada da SAGA de alteração de perfil");
        } catch (Exception ex) {
            responseRegistry.cancel(sagaId);
            clienteAlteradoPorSaga.remove(sagaId);
            sagaPersistenceService.failSaga(
                sagaId,
                "SAGA de alteração de perfil não concluída: " + ex.getMessage()
            );
            throw new IllegalStateException(
                "SAGA de alteração de perfil não concluída: " + ex.getMessage(), ex);
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
        clienteAlteradoPorSaga.put(event.getSagaId(), event);

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
            clienteAlteradoPorSaga.remove(event.getSagaId());

        if (clienteEvent == null) {
            fail(event.getSagaId(), 
                event.getCpf(), 
                "Evento de conta recebido sem dados parciais do cliente");
            return;
        }

        AlterarPerfilSagaResponse response = AlterarPerfilSagaResponse.fromEvent(clienteEvent, event);

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
        sagaPersistenceService.failStep(
            event.sagaId(),
            STEP_ALTERAR_LIMITE_CONTA,
            event.motivo()
        );

        sagaPersistenceService.requireCompensation(
            event.sagaId(),
            "Falha no MS Conta: " + event.motivo()
        );

        /*
         * Aqui futuramente entra:
         *
         * 1. Buscar no banco o step ALTERAR_PERFIL_CLIENTE.
         * 2. Ler o payload/response_payload antigo em JSONB.
         * 3. Publicar cliente.reverter-alteracao-perfil.command.
         * 4. Marcar a SAGA como COMPENSATED ou FAILED.
         */

        fail(event.sagaId(), event.cpf(), "Falha no MS Conta: " + event.motivo());
    }

    private void fail(String sagaId, String cpf, String motivo) {
        log.warn("SAGA {} falhou para CPF {}: {}", sagaId, cpf, motivo);
        
        clienteAlteradoPorSaga.remove(sagaId);

        sagaPersistenceService.failSaga(sagaId, motivo);

        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivo));
    }

}
