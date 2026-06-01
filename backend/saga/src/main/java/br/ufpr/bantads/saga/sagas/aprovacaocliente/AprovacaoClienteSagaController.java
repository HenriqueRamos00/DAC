package br.ufpr.bantads.saga.sagas.aprovacaocliente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.request.AprovarClienteSagaRequest;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.response.ClienteAprovadoSagaResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sagas/clientes")
@RequiredArgsConstructor
public class AprovacaoClienteSagaController {

    private final AprovacaoClienteOrchestrator orchestrator;

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<Object> aprovar(
        @PathVariable String cpf
    ) {
        AprovarClienteSagaRequest request = new AprovarClienteSagaRequest(cpf);

        Object result = orchestrator.iniciar(request);
        System.out.println(result);
        if (result instanceof ClienteAprovadoSagaResponse response) {
            System.out.println("caiu ok");
            return ResponseEntity.ok(response);
        }

        if (result instanceof SagaErrorResponse error) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new SagaErrorResponse(null, "FAILED", "Resposta inesperada da SAGA"));
    }
}
