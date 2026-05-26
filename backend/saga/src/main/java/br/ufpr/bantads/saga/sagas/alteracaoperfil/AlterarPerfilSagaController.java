package br.ufpr.bantads.saga.sagas.alteracaoperfil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.request.AlterarPerfilRequest;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.response.AlterarPerfilSagaResponse;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.AlteracaoPerfilOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/sagas/clientes")
@RequiredArgsConstructor
public class AlterarPerfilSagaController {

    private final AlteracaoPerfilOrchestrator orchestrator;

    @PutMapping("/{cpf}/perfil")
    public ResponseEntity<AlterarPerfilSagaResponse> putMethodName(
        @PathVariable String cpf,
        @RequestBody AlterarPerfilRequest request) {
        
        return ResponseEntity.ok(orchestrator.iniciar(cpf, request));
    }

}
