package br.ufpr.bantads.saga.sagas.remocaogerente;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RemocaoGerenteOrchestrator {

    public static final String MOTIVO_ULTIMO_GERENTE = "ULTIMO_GERENTE";

    private final RemocaoGerenteCommandPublisher commandPublisher;
    private final SagaResponseRegistry responseRegistry;

    private final Map<String, String> cpfRemovendoPorSaga = new ConcurrentHashMap<>();
    private final Map<String, ContasReatribuidasEvent> contasReatribuidasPorSaga = new ConcurrentHashMap<>();
    private final Map<String, String> statusPorSaga = new ConcurrentHashMap<>();

    @Value("${saga.step.timeout-ms:10000}")
    private Long timeoutMs;

    public Object iniciar(String cpf) {
        String sagaId = UUID.randomUUID().toString();
        cpfRemovendoPorSaga.put(sagaId, cpf);
        statusPorSaga.put(sagaId, "STARTED");

        CompletableFuture<Object> future = responseRegistry.register(sagaId);

        statusPorSaga.put(sagaId, "LISTAR_GERENTES_ATIVOS_SOLICITADO");
        commandPublisher.publishListarGerentesAtivos(new ListarGerentesAtivosCommand(sagaId, cpf));

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("SAGA remoção de gerente {} falhou no aguardo", sagaId, ex);
            responseRegistry.cancel(sagaId);
            cleanup(sagaId);
            statusPorSaga.put(sagaId, "FAILED");
            return new SagaErrorResponse(sagaId, "FAILED", "SAGA não concluída: " + ex.getMessage());
        }
    }

    public void handleGerentesAtivosListados(GerentesAtivosListadosEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} recebeu gerentes ativos: {}", sagaId, event.getCpfs());
        statusPorSaga.put(sagaId, "GERENTES_ATIVOS_LISTADOS");

        String cpfRemovendo = cpfRemovendoPorSaga.get(sagaId);
        if (cpfRemovendo == null) {
            fail(sagaId, "Estado do cpf removendo perdido entre steps");
            return;
        }

        statusPorSaga.put(sagaId, "REATRIBUIR_CONTAS_SOLICITADO");
        commandPublisher.publishReatribuirContasGerente(
            ReatribuirContasGerenteCommand.fromGerentesAtivos(cpfRemovendo, event)
        );
    }

    public void handleContasReatribuidas(ContasReatribuidasEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} recebeu contas reatribuídas: {} para {}",
            sagaId, event.getContasReatribuidas(), event.getGerenteDestinoCpf());
        contasReatribuidasPorSaga.put(sagaId, event);
        statusPorSaga.put(sagaId, "CONTAS_REATRIBUIDAS");

        String cpfRemovendo = cpfRemovendoPorSaga.get(sagaId);
        if (cpfRemovendo == null) {
            fail(sagaId, "Estado do cpf removendo perdido antes da remoção");
            return;
        }

        statusPorSaga.put(sagaId, "REMOVER_GERENTE_SOLICITADO");
        commandPublisher.publishRemoverGerente(new RemoverGerenteCommand(sagaId, cpfRemovendo));
    }

    public void handleGerenteRemovido(GerenteRemovidoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} concluída — gerente removido: {}", sagaId, event.getCpf());

        ContasReatribuidasEvent contasEvent = contasReatribuidasPorSaga.get(sagaId);
        if (contasEvent == null) {
            fail(sagaId, "Estado das contas reatribuídas perdido no último step");
            return;
        }

        statusPorSaga.put(sagaId, "COMPLETED");
        cleanup(sagaId);
        responseRegistry.complete(sagaId, RemocaoGerenteResponse.fromContasReatribuidas(event.getCpf(), contasEvent));
    }

    public void handleListagemGerentesAtivosFalhou(ListagemGerentesAtivosFalhouEvent event) {
        fail(event.getSagaId(), event.getMotivo());
    }

    public void handleReatribuicaoContasFalhou(ReatribuicaoContasFalhouEvent event) {
        fail(event.getSagaId(), "Falha ao reatribuir contas: " + event.getMotivo());
    }

    public void handleRemocaoGerenteFalhou(RemocaoGerenteFalhouEvent event) {
        statusPorSaga.put(event.getSagaId(), "COMPENSATION_REQUIRED");
        // TODO Aqui entraria o comando de compensação para reverter a reatribuição de contas.
        fail(event.getSagaId(), "Falha ao remover gerente: " + event.getMotivo());
    }

    private void fail(String sagaId, String motivo) {
        log.warn("SAGA {} falhou: {}", sagaId, motivo);
        statusPorSaga.put(sagaId, "FAILED");
        cleanup(sagaId);
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivo));
    }

    private void cleanup(String sagaId) {
        cpfRemovendoPorSaga.remove(sagaId);
        contasReatribuidasPorSaga.remove(sagaId);
    }
}