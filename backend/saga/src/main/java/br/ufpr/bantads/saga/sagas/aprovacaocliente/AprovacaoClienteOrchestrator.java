package br.ufpr.bantads.saga.sagas.aprovacaocliente;

import static br.ufpr.bantads.saga.sagas.aprovacaocliente.AprovacaoClienteStep.APROVAR_CLIENTE;
import static br.ufpr.bantads.saga.sagas.aprovacaocliente.AprovacaoClienteStep.CONSULTAR_CLIENTE;
import static br.ufpr.bantads.saga.sagas.aprovacaocliente.AprovacaoClienteStep.CRIAR_CONTA;
import static br.ufpr.bantads.saga.sagas.aprovacaocliente.AprovacaoClienteStep.CRIAR_USUARIO_CLIENTE;
import static br.ufpr.bantads.saga.sagas.aprovacaocliente.AprovacaoClienteStep.LISTAR_GERENTES_ATIVOS;
import static br.ufpr.bantads.saga.sagas.aprovacaocliente.AprovacaoClienteStep.SELECIONAR_GERENTE_PARA_NOVA_CONTA;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.AprovarClienteCommand;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.CriarContaCommand;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.CriarUsuarioClienteCommand;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.ListarGerentesAtivosCommand;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.SelecionarGerenteParaNovaContaCommand;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.AprovacaoClienteFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ClienteAprovadoEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ContaCriadaSagaEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.CriacaoContaFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.CriacaoUsuarioClienteFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.GerenteParaNovaContaSelecionadoEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.GerentesAtivosListadosEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ListagemGerentesAtivosFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.SelecaoGerenteParaNovaContaFalhouEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.UsuarioClienteCriadoEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.request.AprovarClienteSagaRequest;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.response.ClienteAprovadoSagaResponse;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.shared.ClienteAprovacaoDados;
import br.ufpr.bantads.saga.shared.SagaResponseRegistry;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.shared.enums.SagaStatus;
import br.ufpr.bantads.saga.shared.service.SagaPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AprovacaoClienteOrchestrator {

    private static final String SAGA_TYPE = "APROVACAO_CLIENTE";

    private final AprovacaoClienteCommandPublisher commandPublisher;
    private final SagaResponseRegistry responseRegistry;
    private final SagaPersistenceService sagaPersistenceService;
    private final RestClient clienteRestClient;

    @Value("${saga.step.timeout-ms:10000}")
    private Long timeoutMs;

    public Object iniciar(AprovarClienteSagaRequest request) {
        String sagaId = UUID.randomUUID().toString();

        sagaPersistenceService.createSaga(sagaId, SAGA_TYPE);
        sagaPersistenceService.markStepSent(
            sagaId,
            CONSULTAR_CLIENTE.order(),
            CONSULTAR_CLIENTE.stepName(),
            AprovarClienteSagaRequest.class.getSimpleName(),
            request,
            SagaStatus.EXECUTING
        );

        ClienteAprovacaoDados cliente;

        try {
            cliente = consultarCliente(request.cpf());
            sagaPersistenceService.markStepCompleted(
                sagaId,
                CONSULTAR_CLIENTE.stepName(),
                ClienteAprovacaoDados.class.getSimpleName(),
                cliente
            );
        } catch (Exception ex) {
            log.warn("SAGA aprovação cliente {} não conseguiu consultar cliente {}", sagaId, request.cpf(), ex);
            failPersistedStep(
                sagaId,
                CONSULTAR_CLIENTE.stepName(),
                "Falha ao consultar cliente: " + ex.getMessage()
            );

            return new SagaErrorResponse(
                sagaId,
                "FAILED",
                "Falha ao consultar cliente: " + ex.getMessage()
            );
        }

        var future = responseRegistry.register(sagaId);
        CriarUsuarioClienteCommand command =
            new CriarUsuarioClienteCommand(sagaId, cliente.cpf(), cliente.email());

        sagaPersistenceService.markStepSent(
            sagaId,
            CRIAR_USUARIO_CLIENTE.order(),
            CRIAR_USUARIO_CLIENTE.stepName(),
            CriarUsuarioClienteCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );

        commandPublisher.publishCriarUsuarioCliente(command);

        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            log.error("SAGA aprovação cliente {} falhou no aguardo", sagaId, ex);
            responseRegistry.cancel(sagaId);
            sagaPersistenceService.failSaga(
                sagaId,
                "SAGA de aprovação de cliente não concluída: " + ex.getMessage()
            );
            return new SagaErrorResponse(sagaId, "FAILED", "SAGA não concluída: " + ex.getMessage());
        }
    }

    public void handleUsuarioClienteCriado(UsuarioClienteCriadoEvent event) {
        sagaPersistenceService.markStepCompleted(
            event.sagaId(),
            CRIAR_USUARIO_CLIENTE.stepName(),
            UsuarioClienteCriadoEvent.class.getSimpleName(),
            event
        );

        ListarGerentesAtivosCommand command = new ListarGerentesAtivosCommand(event.sagaId());

        sagaPersistenceService.markStepSent(
            event.sagaId(),
            LISTAR_GERENTES_ATIVOS.order(),
            LISTAR_GERENTES_ATIVOS.stepName(),
            ListarGerentesAtivosCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );

        commandPublisher.publishListarGerentesAtivos(command);
    }

    public void handleGerentesAtivosListados(GerentesAtivosListadosEvent event) {
        sagaPersistenceService.markStepCompleted(
            event.sagaId(),
            LISTAR_GERENTES_ATIVOS.stepName(),
            GerentesAtivosListadosEvent.class.getSimpleName(),
            event
        );

        SelecionarGerenteParaNovaContaCommand command =
            new SelecionarGerenteParaNovaContaCommand(event.sagaId(), event.gerentes());

        sagaPersistenceService.markStepSent(
            event.sagaId(),
            SELECIONAR_GERENTE_PARA_NOVA_CONTA.order(),
            SELECIONAR_GERENTE_PARA_NOVA_CONTA.stepName(),
            SelecionarGerenteParaNovaContaCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );

        commandPublisher.publishSelecionarGerenteParaNovaConta(command);
    }

    public void handleGerenteParaNovaContaSelecionado(GerenteParaNovaContaSelecionadoEvent event) {
        String sagaId = event.sagaId();

        sagaPersistenceService.markStepCompleted(
            sagaId,
            SELECIONAR_GERENTE_PARA_NOVA_CONTA.stepName(),
            GerenteParaNovaContaSelecionadoEvent.class.getSimpleName(),
            event
        );

        ClienteAprovacaoDados cliente;
        try {
            cliente = sagaPersistenceService.getCompletedStepResponse(
                sagaId,
                CONSULTAR_CLIENTE.stepName(),
                ClienteAprovacaoDados.class
            );
        } catch (Exception ex) {
            fail(sagaId, SELECIONAR_GERENTE_PARA_NOVA_CONTA, "Estado do cliente perdido antes da criação de conta");
            return;
        }

        CriarContaCommand command = CriarContaCommand.from(sagaId, cliente, event);

        sagaPersistenceService.markStepSent(
            sagaId,
            CRIAR_CONTA.order(),
            CRIAR_CONTA.stepName(),
            CriarContaCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );

        commandPublisher.publishCriarConta(command);
    }

    public void handleContaCriada(ContaCriadaSagaEvent event) {
        sagaPersistenceService.markStepCompleted(
            event.sagaId(),
            CRIAR_CONTA.stepName(),
            ContaCriadaSagaEvent.class.getSimpleName(),
            event
        );

        UsuarioClienteCriadoEvent usuario;
        try {
            usuario = sagaPersistenceService.getCompletedStepResponse(
                event.sagaId(),
                CRIAR_USUARIO_CLIENTE.stepName(),
                UsuarioClienteCriadoEvent.class
            );
        } catch (Exception ex) {
            fail(event.sagaId(), CRIAR_CONTA, "Estado do usuário perdido antes da aprovação do cliente");
            return;
        }

        AprovarClienteCommand command =
            new AprovarClienteCommand(event.sagaId(), event.clienteCpf(), usuario.senhaGerada());

        sagaPersistenceService.markStepSent(
            event.sagaId(),
            APROVAR_CLIENTE.order(),
            APROVAR_CLIENTE.stepName(),
            AprovarClienteCommand.class.getSimpleName(),
            command,
            SagaStatus.EXECUTING
        );

        commandPublisher.publishAprovarCliente(command);
    }

    public void handleClienteAprovado(ClienteAprovadoEvent event) {
        sagaPersistenceService.markStepCompleted(
            event.sagaId(),
            APROVAR_CLIENTE.stepName(),
            ClienteAprovadoEvent.class.getSimpleName(),
            event
        );
        sagaPersistenceService.completeSaga(event.sagaId());

        responseRegistry.complete(event.sagaId(), ClienteAprovadoSagaResponse.fromEvent(event));
    }

    public void handleCriacaoUsuarioClienteFalhou(CriacaoUsuarioClienteFalhouEvent event) {
        fail(event.sagaId(), CRIAR_USUARIO_CLIENTE, "Falha ao criar usuário cliente: " + event.motivo());
    }

    public void handleListagemGerentesAtivosFalhou(ListagemGerentesAtivosFalhouEvent event) {
        fail(event.sagaId(), LISTAR_GERENTES_ATIVOS, "Falha ao listar gerentes ativos: " + event.motivo());
    }

    public void handleSelecaoGerenteParaNovaContaFalhou(SelecaoGerenteParaNovaContaFalhouEvent event) {
        fail(event.sagaId(), SELECIONAR_GERENTE_PARA_NOVA_CONTA, "Falha ao selecionar gerente: " + event.motivo());
    }

    public void handleCriacaoContaFalhou(CriacaoContaFalhouEvent event) {
        fail(event.sagaId(), CRIAR_CONTA, "Falha ao criar conta: " + event.motivo());
    }

    public void handleAprovacaoClienteFalhou(AprovacaoClienteFalhouEvent event) {
        fail(event.sagaId(), APROVAR_CLIENTE, "Falha ao aprovar cliente: " + event.motivo());
    }

    private void fail(String sagaId, AprovacaoClienteStep step, String motivo) {
        log.warn("SAGA aprovação cliente {} falhou: {}", sagaId, motivo);

        failPersistedStep(sagaId, step.stepName(), motivo);
        responseRegistry.complete(sagaId, new SagaErrorResponse(sagaId, "FAILED", motivo));
    }

    private void failPersistedStep(String sagaId, String stepName, String motivo) {
        sagaPersistenceService.failStep(sagaId, stepName, motivo);
        sagaPersistenceService.failSaga(sagaId, motivo);
    }

    private ClienteAprovacaoDados consultarCliente(String cpf) {
        ClienteAprovacaoDados cliente = clienteRestClient
            .get()
            .uri("/clientes/{cpf}", cpf)
            .retrieve()
            .body(ClienteAprovacaoDados.class);

        if (cliente == null) {
            throw new RestClientException("MS Cliente retornou corpo vazio");
        }

        return cliente;
    }
}
