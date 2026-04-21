package com.ufpr.bantads.cliente.presentation.rest;

import java.util.List;

import com.ufpr.bantads.cliente.application.dto.request.AutocadastroRequest;
import com.ufpr.bantads.cliente.application.dto.response.ClienteParaAprovarResponse;
import com.ufpr.bantads.cliente.application.dto.response.ClienteResponse;
import com.ufpr.bantads.cliente.application.usecase.CriarClientePendenteUseCase;
import com.ufpr.bantads.cliente.application.usecase.GetClienteByCpfUseCase;
import com.ufpr.bantads.cliente.application.usecase.ListAllClientesUseCase;
import com.ufpr.bantads.cliente.application.usecase.ListClientesParaAprovarUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClienteController {

    private final CriarClientePendenteUseCase criarClientePendenteUseCase;
    private final ListAllClientesUseCase listAllClientesUseCase;
    private final ListClientesParaAprovarUseCase listClientesParaAprovarUseCase;
    private final GetClienteByCpfUseCase getClienteByCpfUseCase;

    //só para testes remover dps e deixar só via mensageria rabbitmq
    @PostMapping("/clientes")
    public ResponseEntity<?> autocadastro(@Valid @RequestBody AutocadastroRequest request) {
        criarClientePendenteUseCase.execute(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/clientes")
    public ResponseEntity<?> getClientes(@RequestParam(required = false) String filtro) {
        if ("para_aprovar".equals(filtro)) {
            List<ClienteParaAprovarResponse> clientes = listClientesParaAprovarUseCase.execute();
            return ResponseEntity.ok(clientes);
        }

        List<ClienteResponse> clientes = listAllClientesUseCase.execute();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/clientes/{cpf}")
    public ResponseEntity<ClienteResponse> getClienteByCpf(@PathVariable String cpf) {
        ClienteResponse cliente = getClienteByCpfUseCase.execute(cpf);
        return ResponseEntity.ok(cliente);
    }
}
