package br.ufpr.bantads.saga.sagas.insercaogerente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.request.InserirGerenteRequest;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.response.GerenteResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.sagas.insercaogerente.InsercaoGerenteOrchestrator;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sagas/gerentes")
@RequiredArgsConstructor
public class InsercaoGerenteSagaController {

    private static final String MOTIVO_CPF_DUPLICADO = "CPF_DUPLICADO";

    private final InsercaoGerenteOrchestrator orchestrator;

    @PostMapping
    public ResponseEntity<Object> inserir(@RequestBody InserirGerenteRequest request) {
        Object result = orchestrator.iniciar(request);

        if (result instanceof GerenteResponse response) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        if (result instanceof SagaErrorResponse error) {
            HttpStatus status = isDuplicado(error.motivo()) ? HttpStatus.CONFLICT : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(error);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new SagaErrorResponse(null, "FAILED", "Resposta inesperada da SAGA"));
    }

    private boolean isDuplicado(String motivo) {
        if (motivo == null) {
            return false;
        }
        String lower = motivo.toLowerCase();
        return lower.contains(MOTIVO_CPF_DUPLICADO.toLowerCase())
            || lower.contains("duplicado")
            || lower.contains("já existe")
            || lower.contains("ja existe");
    }
}