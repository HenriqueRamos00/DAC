package br.ufpr.bantads.saga.sagas.insercaogerente;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.AtribuirGerenteContaCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.ConsultarGerenteMaisContasCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.InserirGerenteCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.AtribuicaoGerenteContaFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.ConsultaGerenteMaisContasFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteAtribuidoContaEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteInseridoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteMaisContasConsultadoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.InsercaoGerenteFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.request.InserirGerenteRequest;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.response.GerenteResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.shared.SagaResponseRegistry;
import br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteCommandPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsercaoGerenteOrchestrator {

    private final InsercaoGerenteCommandPublisher commandPublisher;
    private final SagaResponseRegistry responseRegistry;

    private final Map<String, InserirGerenteRequest> requestPorSaga = new ConcurrentHashMap<>();
    private final Map<String, GerenteInseridoEvent> gerenteInseridoPorSaga = new ConcurrentHashMap<>();
    private final Map<String, String> gerenteOriginalPorSaga = new ConcurrentHashMap<>();
    private final Map<String, String> statusPorSaga = new ConcurrentHashMap<>();

    @Value("${saga.step.timeout-ms:10000}")
    private Long timeoutMs;

    public Object iniciar(InserirGerenteRequest request) {
        String sagaId = UUID.randomUUID().toString();
        requestPorSaga.put(sagaId, request);
        statusPorSaga.put(sagaId, "STARTED");

        CompletableFuture<Object> future = responseRegistry.register(sagaId);

        statusPorSaga.put(sagaId, "CONSULTAR_GERENTE_MAIS_CONTAS_SOLICITADO");
        commandPublisher.publishConsultarGerenteMaisContas(new ConsultarGerenteMaisContasCommand(sagaId));

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("SAGA inserir gerente {} falhou no aguardo", sagaId, ex);
            responseRegistry.cancel(sagaId);
            cleanup(sagaId);
            statusPorSaga.put(sagaId, "FAILED");
            return new SagaErrorResponse(sagaId, "FAILED", "SAGA não concluída: " + ex.getMessage());
        }
    }

    public void handleGerenteMaisContasConsultado(GerenteMaisContasConsultadoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} recebeu gerente com mais contas: {}", sagaId, event.getCpf());
        gerenteOriginalPorSaga.put(sagaId, event.getCpf());
        statusPorSaga.put(sagaId, "GERENTE_MAIS_CONTAS_CONSULTADO");

        InserirGerenteRequest request = requestPorSaga.get(sagaId);
        if (request == null) {
            fail(sagaId, "Estado do request perdido entre steps");
            return;
        }

        statusPorSaga.put(sagaId, "GERENTE_INSERIR_SOLICITADO");
        commandPublisher.publishInserirGerente(InserirGerenteCommand.fromRequest(sagaId, request));
    }

    public void handleGerenteInserido(GerenteInseridoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} recebeu gerente inserido: {}", sagaId, event.getCpf());
        gerenteInseridoPorSaga.put(sagaId, event);
        statusPorSaga.put(sagaId, "GERENTE_INSERIDO");

        String gerenteOriginalCpf = gerenteOriginalPorSaga.get(sagaId);
        if (gerenteOriginalCpf == null) {
            fail(sagaId, "Estado do gerente original perdido entre steps");
            return;
        }

        statusPorSaga.put(sagaId, "ATRIBUIR_GERENTE_CONTA_SOLICITADO");
        commandPublisher.publishAtribuirGerenteConta(
            AtribuirGerenteContaCommand.fromGerenteInserido(gerenteOriginalCpf, event)
        );
    }

    public void handleGerenteAtribuidoConta(GerenteAtribuidoContaEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} concluída — contas reatribuídas: {}", sagaId, event.getContasReatribuidas());

        GerenteInseridoEvent gerenteEvent = gerenteInseridoPorSaga.get(sagaId);
        if (gerenteEvent == null) {
            fail(sagaId, "Estado do gerente inserido perdido no último step");
            return;
        }

        statusPorSaga.put(sagaId, "COMPLETED");
        cleanup(sagaId);
        responseRegistry.complete(sagaId, GerenteResponse.fromEvent(gerenteEvent));
    }

    public void handleConsultaGerenteMaisContasFalhou(ConsultaGerenteMaisContasFalhouEvent event) {
        fail(event.getSagaId(), "Falha ao consultar gerente com mais contas: " + event.getMotivo());
    }

    public void handleInsercaoGerenteFalhou(InsercaoGerenteFalhouEvent event) {
        fail(event.getSagaId(), event.getMotivo());
    }

    public void handleAtribuicaoGerenteContaFalhou(AtribuicaoGerenteContaFalhouEvent event) {
        statusPorSaga.put(event.getSagaId(), "COMPENSATION_REQUIRED");
        // TODO Aqui entraria o comando de compensação para reverter a inserção do gerente.
        fail(event.getSagaId(), "Falha ao atribuir contas ao novo gerente: " + event.getMotivo());
    }

    private void fail(String sagaId, String motivo) {
        log.warn("SAGA {} falhou: {}", sagaId, motivo);
        statusPorSaga.put(sagaId, "FAILED");
        cleanup(sagaId);
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivo));
    }

    private void cleanup(String sagaId) {
        requestPorSaga.remove(sagaId);
        gerenteInseridoPorSaga.remove(sagaId);
        gerenteOriginalPorSaga.remove(sagaId);
    }
}