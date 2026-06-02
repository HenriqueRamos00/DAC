package br.ufpr.bantads.saga.sagas.remocaogerente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga.sagas.remocaogerente.dto.response.RemocaoGerenteResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sagas/gerentes")
@RequiredArgsConstructor
public class RemocaoGerenteSagaController {

    private final RemocaoGerenteOrchestrator orchestrator;

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Object> remover(@PathVariable String cpf) {
        Object result = orchestrator.iniciar(cpf);

        if (result instanceof RemocaoGerenteResponse response) {
            return ResponseEntity.ok(response);
        }

        if (result instanceof SagaErrorResponse error) {
            HttpStatus status = isUltimoGerente(error.motivo())
                ? HttpStatus.CONFLICT
                : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(error);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new SagaErrorResponse(null, "FAILED", "Resposta inesperada da SAGA"));
    }

    private boolean isUltimoGerente(String motivo) {
        return motivo != null && motivo.contains(RemocaoGerenteOrchestrator.MOTIVO_ULTIMO_GERENTE);
    }
}