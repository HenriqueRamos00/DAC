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
import br.ufpr.bantads.saga.shared.SagaResponseRegistry;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.AlteracaoPerfilCommandPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlteracaoPerfilOrchestrator {

    private final AlteracaoPerfilCommandPublisher commandPublisher;
    private final SagaResponseRegistry responseRegistry;

    private final Map<String, ClientePerfilAlteradoEvent> clienteAlteradoPorSaga = new ConcurrentHashMap<>();
    private final Map<String, String> statusPorSaga = new ConcurrentHashMap<>();

    @Value("${saga.step.timeout-ms:10000}")
    private Long timeoutMs;

    public AlterarPerfilSagaResponse iniciar(String cpf, AlterarPerfilRequest request) {
        String sagaId = UUID.randomUUID().toString();

        statusPorSaga.put(sagaId, "STARTED");

        CompletableFuture<Object> future = responseRegistry.register(sagaId);

        AlterarPerfilClienteCommand command = AlterarPerfilClienteCommand.fromRequest(cpf, sagaId, request);

        statusPorSaga.put(sagaId, "CLIENTE_ALTERACAI_SOLICITADA");
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
            statusPorSaga.put(sagaId, "FAILED");
            clienteAlteradoPorSaga.remove(sagaId);
            throw new IllegalStateException("SAGA de alteração de perfil não concluída: " + ex.getMessage(), ex);
        }
    }

    public void handleClientePerfilAlterado(ClientePerfilAlteradoEvent event) {
        log.info("SAGA {} recebeu sucesso do MS Cliente", event.getSagaId());
        clienteAlteradoPorSaga.put(event.getSagaId(), event);
        statusPorSaga.put(event.getSagaId(), "CLIENTE_ALTERADO");

        AlterarLimiteContaCommand command = AlterarLimiteContaCommand.fromEvent(event);
        statusPorSaga.put(event.getSagaId(), "CONTA_LIMITE_SOLICITADO");
        commandPublisher.publishAlterarLimite(command);
    }

    public void handleContaLimiteAlterado(ContaLimiteAlteradoEvent event) {
        log.info("SAGA {} recebeu sucesso do MS Conta", event.getSagaId());
        ClientePerfilAlteradoEvent clienteEvent = clienteAlteradoPorSaga.remove(event.getSagaId());

        if (clienteEvent == null) {
            fail(event.getSagaId(), event.getCpf(), "Evento de conta recebido sem dados parciais do cliente");
            return;
        }

        AlterarPerfilSagaResponse response = AlterarPerfilSagaResponse.fromEvent(clienteEvent, event);

        statusPorSaga.put(event.getSagaId(), "COMPLETED");
        responseRegistry.complete(event.getSagaId(), response);
    }

    public void handleClienteAlteracaoPerfilFalhou(ClienteAlteracaoFalhouEvent event) {
        fail(event.sagaId(), event.cpf(), "Falha no MS Cliente: " + event.motivo());
    }

    public void handleContaAlteracaoLimiteFalhou(ClienteAlteracaoFalhouEvent event) {
        statusPorSaga.put(event.sagaId(), "COMPENSATION_REQUIRED");
        // Aqui deve entrar o comando cliente.reverter-alteracao-perfil.command.
        fail(event.sagaId(), event.cpf(), "Falha no MS Conta: " + event.motivo());
    }

    private void fail(String sagaId, String cpf, String motivo) {
        log.warn("SAGA {} falhou para CPF {}: {}", sagaId, cpf, motivo);
        statusPorSaga.put(sagaId, "FAILED");
        clienteAlteradoPorSaga.remove(sagaId);
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivo));
    }

}
