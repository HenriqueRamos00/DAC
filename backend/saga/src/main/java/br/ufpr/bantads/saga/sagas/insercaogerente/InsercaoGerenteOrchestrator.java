package br.ufpr.bantads.saga.sagas.insercaogerente;

import static br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteStep.ATRIBUIR_GERENTE_CONTA;
import static br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteStep.CONSULTAR_GERENTE_MAIS_CONTAS;
import static br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteStep.CRIAR_USUARIO_GERENTE;
import static br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteStep.EXCLUIR_USUARIO_GERENTE_COMPENSACAO;
import static br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteStep.INSERIR_GERENTE;
import static br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteStep.REMOVER_GERENTE_COMPENSACAO;
import static br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteStep.REQUEST_INICIAL;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.AtribuirGerenteContaCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.ConsultarGerenteMaisContasCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.CriarUsuarioGerenteCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.ExcluirUsuarioGerenteCompensacaoCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.InserirGerenteCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.RemoverGerenteCompensacaoCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.AtribuicaoGerenteContaFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.ConsultaGerenteMaisContasFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.CriacaoUsuarioGerenteFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.ExclusaoUsuarioGerenteCompensacaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteAtribuidoContaEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteInseridoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteMaisContasConsultadoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteRemovidoCompensacaoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.InsercaoGerenteFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.RemocaoGerenteCompensacaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.UsuarioGerenteCriadoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.UsuarioGerenteExcluidoCompensacaoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.request.InserirGerenteRequest;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.response.GerenteResponse;
import br.ufpr.bantads.saga.shared.SagaResponseRegistry;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaResult;
import br.ufpr.bantads.saga.shared.enums.SagaStatus;
import br.ufpr.bantads.saga.shared.service.SagaPersistenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsercaoGerenteOrchestrator {

    private static final String SAGA_TYPE = "INSERCAO_GERENTE";

    private final InsercaoGerenteCommandPublisher commandPublisher;
    private final SagaResponseRegistry responseRegistry;
    private final SagaPersistenceService sagaPersistenceService;

    @Value("${saga.step.timeout-ms:10000}")
    private Long timeoutMs;

    public SagaResult iniciar(InserirGerenteRequest request) {
        String sagaId = UUID.randomUUID().toString();
        CompletableFuture<SagaResult> future = responseRegistry.register(sagaId);

        sagaPersistenceService.createSaga(sagaId, SAGA_TYPE);
        persistirRequestInicial(sagaId, request);

        ConsultarGerenteMaisContasCommand command = new ConsultarGerenteMaisContasCommand(sagaId);
        sagaPersistenceService.markStepSent(
            sagaId,
            CONSULTAR_GERENTE_MAIS_CONTAS.order(),
            CONSULTAR_GERENTE_MAIS_CONTAS.stepName(),
            ConsultarGerenteMaisContasCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );
        commandPublisher.publishConsultarGerenteMaisContas(command);

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("SAGA inserir gerente {} falhou no aguardo", sagaId, ex);
            responseRegistry.cancel(sagaId);
            sagaPersistenceService.failSaga(sagaId, "SAGA não concluída: " + ex.getMessage());
            return new SagaErrorResponse(sagaId, "FAILED", "SAGA não concluída: " + ex.getMessage());
        }
    }

    public void handleGerenteMaisContasConsultado(GerenteMaisContasConsultadoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} recebeu gerente com mais contas: {}", sagaId, event.getCpf());

        sagaPersistenceService.markStepCompleted(
            sagaId,
            CONSULTAR_GERENTE_MAIS_CONTAS.stepName(),
            GerenteMaisContasConsultadoEvent.class.getSimpleName(),
            event
        );

        InserirGerenteRequest request;
        try {
            request = sagaPersistenceService.getCompletedStepResponse(
                sagaId, REQUEST_INICIAL.stepName(), InserirGerenteRequest.class
            );
        } catch (Exception ex) {
            fail(sagaId, CONSULTAR_GERENTE_MAIS_CONTAS, "Estado do request perdido entre steps");
            return;
        }

        InserirGerenteCommand command = InserirGerenteCommand.fromRequest(sagaId, request);
        sagaPersistenceService.markStepSent(
            sagaId,
            INSERIR_GERENTE.order(),
            INSERIR_GERENTE.stepName(),
            InserirGerenteCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );
        commandPublisher.publishInserirGerente(command);
    }

    public void handleGerenteInserido(GerenteInseridoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} recebeu gerente inserido: {}", sagaId, event.getCpf());

        sagaPersistenceService.markStepCompleted(
            sagaId,
            INSERIR_GERENTE.stepName(),
            GerenteInseridoEvent.class.getSimpleName(),
            event
        );

        InserirGerenteRequest request;
        try {
            request = sagaPersistenceService.getCompletedStepResponse(
                sagaId, REQUEST_INICIAL.stepName(), InserirGerenteRequest.class
            );
        } catch (Exception ex) {
            fail(sagaId, INSERIR_GERENTE, "Estado do request perdido antes de criar usuário no auth");
            return;
        }

        CriarUsuarioGerenteCommand command = CriarUsuarioGerenteCommand.fromGerenteInserido(event, request);
        sagaPersistenceService.markStepSent(
            sagaId,
            CRIAR_USUARIO_GERENTE.order(),
            CRIAR_USUARIO_GERENTE.stepName(),
            CriarUsuarioGerenteCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );
        commandPublisher.publishCriarUsuarioGerente(command);
    }

    public void handleUsuarioGerenteCriado(UsuarioGerenteCriadoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} recebeu usuário criado no auth para cpf {}", sagaId, event.getCpf());

        sagaPersistenceService.markStepCompleted(
            sagaId,
            CRIAR_USUARIO_GERENTE.stepName(),
            UsuarioGerenteCriadoEvent.class.getSimpleName(),
            event
        );

        GerenteMaisContasConsultadoEvent gerenteOriginal;
        GerenteInseridoEvent gerenteInserido;
        try {
            gerenteOriginal = sagaPersistenceService.getCompletedStepResponse(
                sagaId, CONSULTAR_GERENTE_MAIS_CONTAS.stepName(), GerenteMaisContasConsultadoEvent.class
            );
            gerenteInserido = sagaPersistenceService.getCompletedStepResponse(
                sagaId, INSERIR_GERENTE.stepName(), GerenteInseridoEvent.class
            );
        } catch (Exception ex) {
            fail(sagaId, CRIAR_USUARIO_GERENTE, "Estado do gerente perdido antes de atribuir conta");
            return;
        }

        AtribuirGerenteContaCommand command = AtribuirGerenteContaCommand.fromGerenteInserido(
            gerenteOriginal.getCpf(), gerenteInserido
        );
        sagaPersistenceService.markStepSent(
            sagaId,
            ATRIBUIR_GERENTE_CONTA.order(),
            ATRIBUIR_GERENTE_CONTA.stepName(),
            AtribuirGerenteContaCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );
        commandPublisher.publishAtribuirGerenteConta(command);
    }

    public void handleGerenteAtribuidoConta(GerenteAtribuidoContaEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} concluída — contas reatribuídas: {}", sagaId, event.getContasReatribuidas());

        sagaPersistenceService.markStepCompleted(
            sagaId,
            ATRIBUIR_GERENTE_CONTA.stepName(),
            GerenteAtribuidoContaEvent.class.getSimpleName(),
            event
        );

        GerenteInseridoEvent gerenteInserido;
        try {
            gerenteInserido = sagaPersistenceService.getCompletedStepResponse(
                sagaId, INSERIR_GERENTE.stepName(), GerenteInseridoEvent.class
            );
        } catch (Exception ex) {
            fail(sagaId, ATRIBUIR_GERENTE_CONTA, "Estado do gerente inserido perdido no último step");
            return;
        }

        sagaPersistenceService.completeSaga(sagaId);
        responseRegistry.complete(sagaId, GerenteResponse.fromEvent(gerenteInserido));
    }

    public void handleConsultaGerenteMaisContasFalhou(ConsultaGerenteMaisContasFalhouEvent event) {
        fail(event.getSagaId(), CONSULTAR_GERENTE_MAIS_CONTAS,
            "Falha ao consultar gerente com mais contas: " + event.getMotivo());
    }

    public void handleInsercaoGerenteFalhou(InsercaoGerenteFalhouEvent event) {
        fail(event.getSagaId(), INSERIR_GERENTE, event.getMotivo());
    }

    public void handleCriacaoUsuarioGerenteFalhou(CriacaoUsuarioGerenteFalhouEvent event) {
        String sagaId = event.getSagaId();
        String motivoOriginal = "Falha ao criar usuário no auth: " + event.getMotivo();

        sagaPersistenceService.failStep(sagaId, CRIAR_USUARIO_GERENTE.stepName(), motivoOriginal);
        compensarGerenteInserido(sagaId, motivoOriginal);
    }

    public void handleAtribuicaoGerenteContaFalhou(AtribuicaoGerenteContaFalhouEvent event) {
        String sagaId = event.getSagaId();
        String motivoOriginal = "Falha ao atribuir contas ao novo gerente: " + event.getMotivo();

        sagaPersistenceService.failStep(sagaId, ATRIBUIR_GERENTE_CONTA.stepName(), motivoOriginal);
        compensarUsuarioGerenteCriado(sagaId, motivoOriginal);
    }

    public void handleUsuarioGerenteExcluidoCompensacao(UsuarioGerenteExcluidoCompensacaoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} compensação auth concluída para cpf {}", sagaId, event.getCpf());

        sagaPersistenceService.markStepCompensated(
            sagaId,
            EXCLUIR_USUARIO_GERENTE_COMPENSACAO.stepName(),
            UsuarioGerenteExcluidoCompensacaoEvent.class.getSimpleName(),
            event
        );

        String motivo = sagaPersistenceService.getSagaErrorMessage(sagaId);
        compensarGerenteInserido(sagaId, motivo == null || motivo.isBlank()
            ? "SAGA falhou após criar usuário gerente"
            : motivo);
    }

    public void handleExclusaoUsuarioGerenteCompensacaoFalhou(ExclusaoUsuarioGerenteCompensacaoFalhouEvent event) {
        String sagaId = event.getSagaId();
        String motivo = sagaPersistenceService.getSagaErrorMessage(sagaId);
        String motivoCompensacao = (motivo == null || motivo.isBlank() ? "SAGA falhou" : motivo)
            + " | compensação auth também falhou: " + event.getMotivo();

        log.error("SAGA {} compensação auth falhou: {}", sagaId, event.getMotivo());
        failPersistedStep(sagaId, EXCLUIR_USUARIO_GERENTE_COMPENSACAO.stepName(), motivoCompensacao);
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivoCompensacao));
    }

    public void handleGerenteRemovidoCompensacao(GerenteRemovidoCompensacaoEvent event) {
        String sagaId = event.getSagaId();
        log.info("SAGA {} compensação gerente concluída para cpf {}", sagaId, event.getCpf());

        sagaPersistenceService.markStepCompensated(
            sagaId,
            REMOVER_GERENTE_COMPENSACAO.stepName(),
            GerenteRemovidoCompensacaoEvent.class.getSimpleName(),
            event
        );
        sagaPersistenceService.completeCompensation(sagaId);

        String motivo = sagaPersistenceService.getSagaErrorMessage(sagaId);
        String mensagem = motivo == null || motivo.isBlank()
            ? "SAGA falhou; gerente foi compensado"
            : motivo;
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", mensagem));
    }

    public void handleRemocaoGerenteCompensacaoFalhou(RemocaoGerenteCompensacaoFalhouEvent event) {
        String sagaId = event.getSagaId();
        String motivo = sagaPersistenceService.getSagaErrorMessage(sagaId);
        String motivoCompensacao = (motivo == null || motivo.isBlank() ? "SAGA falhou" : motivo)
            + " | compensação gerente também falhou: " + event.getMotivo();

        log.error("SAGA {} compensação gerente falhou: {}", sagaId, event.getMotivo());
        failPersistedStep(sagaId, REMOVER_GERENTE_COMPENSACAO.stepName(), motivoCompensacao);
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivoCompensacao));
    }

    private void persistirRequestInicial(String sagaId, InserirGerenteRequest request) {
        sagaPersistenceService.markStepSent(
            sagaId,
            REQUEST_INICIAL.order(),
            REQUEST_INICIAL.stepName(),
            InserirGerenteRequest.class.getSimpleName(),
            request,
            SagaStatus.STARTED
        );
        sagaPersistenceService.markStepCompleted(
            sagaId,
            REQUEST_INICIAL.stepName(),
            InserirGerenteRequest.class.getSimpleName(),
            request
        );
    }

    private void compensarGerenteInserido(String sagaId, String motivo) {
        GerenteInseridoEvent gerenteInserido;
        try {
            gerenteInserido = sagaPersistenceService.getCompletedStepResponse(
                sagaId, INSERIR_GERENTE.stepName(), GerenteInseridoEvent.class
            );
        } catch (Exception ex) {
            String motivoCompensacao = motivo + ". Não foi possível localizar o gerente inserido para compensação";
            log.warn("SAGA inserir gerente {} não conseguiu iniciar compensação de gerente", sagaId, ex);
            sagaPersistenceService.failSaga(sagaId, motivoCompensacao);
            responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivoCompensacao));
            return;
        }

        RemoverGerenteCompensacaoCommand command = new RemoverGerenteCompensacaoCommand(
            sagaId, gerenteInserido.getCpf()
        );
        sagaPersistenceService.requireCompensation(sagaId, motivo);
        sagaPersistenceService.markStepSent(
            sagaId,
            REMOVER_GERENTE_COMPENSACAO.order(),
            REMOVER_GERENTE_COMPENSACAO.stepName(),
            RemoverGerenteCompensacaoCommand.class.getSimpleName(),
            command,
            SagaStatus.COMPENSATION_REQUIRED
        );
        commandPublisher.publishRemoverGerenteCompensacao(command);
    }

    private void compensarUsuarioGerenteCriado(String sagaId, String motivo) {
        GerenteInseridoEvent gerenteInserido;
        try {
            gerenteInserido = sagaPersistenceService.getCompletedStepResponse(
                sagaId, INSERIR_GERENTE.stepName(), GerenteInseridoEvent.class
            );
        } catch (Exception ex) {
            String motivoCompensacao = motivo + ". Não foi possível localizar o gerente inserido para compensação auth";
            log.warn("SAGA inserir gerente {} não conseguiu iniciar compensação de usuário auth", sagaId, ex);
            sagaPersistenceService.failSaga(sagaId, motivoCompensacao);
            responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivoCompensacao));
            return;
        }

        ExcluirUsuarioGerenteCompensacaoCommand command = new ExcluirUsuarioGerenteCompensacaoCommand(
            sagaId, gerenteInserido.getCpf(), gerenteInserido.getEmail()
        );
        sagaPersistenceService.requireCompensation(sagaId, motivo);
        sagaPersistenceService.markStepSent(
            sagaId,
            EXCLUIR_USUARIO_GERENTE_COMPENSACAO.order(),
            EXCLUIR_USUARIO_GERENTE_COMPENSACAO.stepName(),
            ExcluirUsuarioGerenteCompensacaoCommand.class.getSimpleName(),
            command,
            SagaStatus.COMPENSATION_REQUIRED
        );
        commandPublisher.publishExcluirUsuarioGerenteCompensacao(command);
    }

    private void fail(String sagaId, InsercaoGerenteStep step, String motivo) {
        log.warn("SAGA inserir gerente {} falhou: {}", sagaId, motivo);
        failPersistedStep(sagaId, step.stepName(), motivo);
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivo));
    }

    private void failPersistedStep(String sagaId, String stepName, String motivo) {
        sagaPersistenceService.failStep(sagaId, stepName, motivo);
        sagaPersistenceService.failSaga(sagaId, motivo);
    }
}