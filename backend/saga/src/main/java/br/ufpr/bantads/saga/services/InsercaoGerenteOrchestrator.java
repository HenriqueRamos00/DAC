package br.ufpr.bantads.saga.services;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.saga.application.dto.command.AtribuirGerenteContaCommand;
import br.ufpr.bantads.saga.application.dto.command.ConsultarGerenteMaisContasCommand;
import br.ufpr.bantads.saga.application.dto.command.InserirGerenteCommand;
import br.ufpr.bantads.saga.application.dto.event.AtribuicaoGerenteContaFalhouEvent;
import br.ufpr.bantads.saga.application.dto.event.ConsultaGerenteMaisContasFalhouEvent;
import br.ufpr.bantads.saga.application.dto.event.GerenteAtribuidoContaEvent;
import br.ufpr.bantads.saga.application.dto.event.GerenteInseridoEvent;
import br.ufpr.bantads.saga.application.dto.event.GerenteMaisContasConsultadoEvent;
import br.ufpr.bantads.saga.application.dto.event.InsercaoGerenteFalhouEvent;
import br.ufpr.bantads.saga.application.dto.request.InserirGerenteRequest;
import br.ufpr.bantads.saga.application.dto.response.GerenteResponse;
import br.ufpr.bantads.saga.application.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.infrastructure.messaging.SagaResponseRegistry;
import br.ufpr.bantads.saga.infrastructure.messaging.publisher.InsercaoGerenteCommandPublisher;

@Service
public class InsercaoGerenteOrchestrator {

    public static final String MOTIVO_CPF_DUPLICADO = "CPF_DUPLICADO";

    private static final Logger log = LoggerFactory.getLogger(InsercaoGerenteOrchestrator.class);

    private final InsercaoGerenteCommandPublisher commandPublisher;
    private final SagaResponseRegistry responseRegistry;

    private final ConcurrentMap<String, InserirGerenteRequest> requestPorSaga = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, GerenteInseridoEvent> gerenteInseridoPorSaga = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> gerenteOriginalPorSaga = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> statusPorSaga = new ConcurrentHashMap<>();

    @Value("${saga.step.timeout-ms:10000}")
    private Long timeoutMs;

    public InsercaoGerenteOrchestrator(
        InsercaoGerenteCommandPublisher commandPublisher,
        SagaResponseRegistry responseRegistry
    ) {
        this.commandPublisher = commandPublisher;
        this.responseRegistry = responseRegistry;
    }

    public Object iniciar(InserirGerenteRequest request) {
        String sagaId = UUID.randomUUID().toString();
        requestPorSaga.put(sagaId, request);
        statusPorSaga.put(sagaId, "STARTED");

        CompletableFuture<Object> future = responseRegistry.register(sagaId);

        statusPorSaga.put(sagaId, "CONSULTAR_GERENTE_MAIS_CONTAS_SOLICITADO");
        commandPublisher.publishConsultarGerenteMaisContas(new ConsultarGerenteMaisContasCommand(sagaId));

        try {
            Object result = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            return result;
        } catch (Exception ex) {
            log.error("SAGA inserir gerente {} falhou no aguardo", sagaId, ex);
            responseRegistry.cancel(sagaId);
            cleanup(sagaId);
            statusPorSaga.put(sagaId, "FAILED");
            return new SagaErrorResponse(sagaId, "FAILED", "SAGA não concluída: " + ex.getMessage());
        }
    }

    public void handleGerenteMaisContasConsultado(GerenteMaisContasConsultadoEvent event) {
        String sagaId = event.sagaId();
        log.info("SAGA {} recebeu gerente com mais contas: {}", sagaId, event.cpf());
        gerenteOriginalPorSaga.put(sagaId, event.cpf());
        statusPorSaga.put(sagaId, "GERENTE_MAIS_CONTAS_CONSULTADO");

        InserirGerenteRequest request = requestPorSaga.get(sagaId);
        if (request == null) {
            fail(sagaId, "Estado do request perdido entre steps");
            return;
        }

        InserirGerenteCommand command = new InserirGerenteCommand(
            sagaId,
            request.cpf(),
            request.nome(),
            request.email(),
            request.senha(),
            request.tipo()
        );

        statusPorSaga.put(sagaId, "GERENTE_INSERIR_SOLICITADO");
        commandPublisher.publishInserirGerente(command);
    }

    public void handleGerenteInserido(GerenteInseridoEvent event) {
        String sagaId = event.sagaId();
        log.info("SAGA {} recebeu gerente inserido: {}", sagaId, event.cpf());
        gerenteInseridoPorSaga.put(sagaId, event);
        statusPorSaga.put(sagaId, "GERENTE_INSERIDO");

        String gerenteOriginalCpf = gerenteOriginalPorSaga.get(sagaId);
        if (gerenteOriginalCpf == null) {
            fail(sagaId, "Estado do gerente original perdido entre steps");
            return;
        }

        AtribuirGerenteContaCommand command = new AtribuirGerenteContaCommand(
            sagaId,
            gerenteOriginalCpf,
            event.cpf()
        );

        statusPorSaga.put(sagaId, "ATRIBUIR_GERENTE_CONTA_SOLICITADO");
        commandPublisher.publishAtribuirGerenteConta(command);
    }

    public void handleGerenteAtribuidoConta(GerenteAtribuidoContaEvent event) {
        String sagaId = event.sagaId();
        log.info("SAGA {} concluída — contas reatribuídas: {}", sagaId, event.contasReatribuidas());

        GerenteInseridoEvent gerenteEvent = gerenteInseridoPorSaga.get(sagaId);
        if (gerenteEvent == null) {
            fail(sagaId, "Estado do gerente inserido perdido no último step");
            return;
        }

        GerenteResponse response = new GerenteResponse(
            gerenteEvent.cpf(),
            gerenteEvent.nome(),
            gerenteEvent.email(),
            gerenteEvent.tipo()
        );

        statusPorSaga.put(sagaId, "COMPLETED");
        cleanup(sagaId);
        responseRegistry.complete(sagaId, response);
    }

    public void handleConsultaGerenteMaisContasFalhou(ConsultaGerenteMaisContasFalhouEvent event) {
        fail(event.sagaId(), "Falha ao consultar gerente com mais contas: " + event.motivo());
    }

    public void handleInsercaoGerenteFalhou(InsercaoGerenteFalhouEvent event) {
        fail(event.sagaId(), event.motivo());
    }

    public void handleAtribuicaoGerenteContaFalhou(AtribuicaoGerenteContaFalhouEvent event) {
        statusPorSaga.put(event.sagaId(), "COMPENSATION_REQUIRED");
        // TODO Aqui entraria o comando de compensação para reverter a inserção do gerente.
        fail(event.sagaId(), "Falha ao atribuir contas ao novo gerente: " + event.motivo());
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
