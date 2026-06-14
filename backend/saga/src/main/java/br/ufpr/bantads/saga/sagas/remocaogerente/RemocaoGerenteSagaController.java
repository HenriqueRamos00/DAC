package br.ufpr.bantads.saga.sagas.remocaogerente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga.sagas.remocaogerente.dto.response.RemocaoGerenteResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorMapper;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sagas/gerentes")
@RequiredArgsConstructor
public class RemocaoGerenteSagaController {

    private final RemocaoGerenteOrchestrator orchestrator;

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Object> remover(@PathVariable String cpf) {
        SagaResult result = orchestrator.iniciar(cpf);

        return switch (result) {
            case RemocaoGerenteResponse response -> ResponseEntity.ok(response);
            case SagaErrorResponse error -> ResponseEntity.status(SagaErrorMapper.toHttpStatus(error)).body(error);
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SagaErrorResponse(null, "FAILED", "Resposta inesperada da SAGA"));
        };
    }
}