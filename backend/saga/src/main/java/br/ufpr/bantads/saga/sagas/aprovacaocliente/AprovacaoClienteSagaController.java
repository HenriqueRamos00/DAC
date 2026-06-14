package br.ufpr.bantads.saga.sagas.aprovacaocliente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.request.AprovarClienteSagaRequest;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.response.ClienteAprovadoSagaResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorMapper;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sagas/clientes")
@RequiredArgsConstructor
public class AprovacaoClienteSagaController {

    private final AprovacaoClienteOrchestrator orchestrator;

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<SagaResult> aprovar(@PathVariable String cpf) {
        AprovarClienteSagaRequest request = new AprovarClienteSagaRequest(cpf);
        SagaResult result = orchestrator.iniciar(request);

        return switch (result) {
            case ClienteAprovadoSagaResponse response -> ResponseEntity.ok(response);
            case SagaErrorResponse error -> ResponseEntity.status(SagaErrorMapper.toHttpStatus(error)).body(error);
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SagaErrorResponse(null, "FAILED", "Resposta inesperada da SAGA"));
        };
    }
}