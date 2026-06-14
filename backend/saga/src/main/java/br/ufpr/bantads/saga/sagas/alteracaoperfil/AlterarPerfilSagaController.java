package br.ufpr.bantads.saga.sagas.alteracaoperfil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.request.AlterarPerfilRequest;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.response.AlterarPerfilSagaResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorMapper;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sagas/clientes")
@RequiredArgsConstructor
public class AlterarPerfilSagaController {

    private final AlteracaoPerfilOrchestrator orchestrator;

    @PutMapping("/{cpf}/perfil")
    public ResponseEntity<Object> alterarPerfil(
        @PathVariable String cpf,
        @RequestBody AlterarPerfilRequest request
    ) {
        SagaResult result = orchestrator.iniciar(cpf, request);

        return switch (result) {
            case AlterarPerfilSagaResponse response -> ResponseEntity.ok(response);
            case SagaErrorResponse error -> ResponseEntity.status(SagaErrorMapper.toHttpStatus(error)).body(error);
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SagaErrorResponse(null, "FAILED", "Resposta inesperada da SAGA"));
        };
    }
}