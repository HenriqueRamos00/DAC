package br.ufpr.bantads.saga.sagas.insercaogerente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.request.InserirGerenteRequest;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.response.GerenteResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorMapper;
import br.ufpr.bantads.saga.shared.dto.response.SagaErrorResponse;
import br.ufpr.bantads.saga.shared.dto.response.SagaResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sagas/gerentes")
@RequiredArgsConstructor
public class InsercaoGerenteSagaController {

    private final InsercaoGerenteOrchestrator orchestrator;

    @PostMapping
    public ResponseEntity<Object> inserir(@RequestBody InserirGerenteRequest request) {
        SagaResult result = orchestrator.iniciar(request);

        return switch (result) {
            case GerenteResponse response -> ResponseEntity.status(HttpStatus.CREATED).body(response);
            case SagaErrorResponse error -> ResponseEntity.status(SagaErrorMapper.toHttpStatus(error)).body(error);
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SagaErrorResponse(null, "FAILED", "Resposta inesperada da SAGA"));
        };
    }
}