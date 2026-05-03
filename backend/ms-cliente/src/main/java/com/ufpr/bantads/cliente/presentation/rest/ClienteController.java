package com.ufpr.bantads.cliente.presentation.rest;

import java.util.List;

import com.ufpr.bantads.cliente.application.dto.request.AlterarPerfilRequest;
import com.ufpr.bantads.cliente.application.dto.request.CriarClientePendenteRequest;
import com.ufpr.bantads.cliente.application.dto.request.RejeitarClienteRequest;
import com.ufpr.bantads.cliente.application.dto.response.ClienteParaAprovarResponse;
import com.ufpr.bantads.cliente.application.dto.response.ClienteResponse;
import com.ufpr.bantads.cliente.application.dto.response.CriarClientePendenteResponse;
import com.ufpr.bantads.cliente.application.usecase.AlterarPerfilUseCase;
import com.ufpr.bantads.cliente.application.usecase.AprovarClienteUseCase;
import com.ufpr.bantads.cliente.application.usecase.CriarClientePendenteUseCase;
import com.ufpr.bantads.cliente.application.usecase.GetClienteByCpfUseCase;
import com.ufpr.bantads.cliente.application.usecase.ListAllClientesUseCase;
import com.ufpr.bantads.cliente.application.usecase.ListClientesParaAprovarUseCase;
import com.ufpr.bantads.cliente.application.usecase.RejeitarClienteUseCase;
import com.ufpr.bantads.cliente.infrastructure.config.ClienteSeedService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final AprovarClienteUseCase aprovarClienteUseCase;
    private final RejeitarClienteUseCase rejeitarClienteUseCase;
    private final AlterarPerfilUseCase alterarPerfilUseCase;
    private final ClienteSeedService clienteSeedService;


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

    @PostMapping("/clientes")
    public ResponseEntity<CriarClientePendenteResponse> autocadastro(
        @Valid @RequestBody CriarClientePendenteRequest request
    ) {
        var cliente = criarClientePendenteUseCase.execute(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CriarClientePendenteResponse.fromEntity(cliente));
    }

    @PostMapping("/clientes/{cpf}/aprovar")
    public ResponseEntity<ClienteResponse> aprovarCliente(@PathVariable String cpf) {
        ClienteResponse cliente = aprovarClienteUseCase.execute(cpf);
        return ResponseEntity.ok(cliente);
    }

    @PostMapping("/clientes/{cpf}/rejeitar")
    public ResponseEntity<ClienteResponse> rejeitarCliente(
        @PathVariable String cpf,
        @Valid @RequestBody RejeitarClienteRequest request
    ) {
        ClienteResponse cliente = rejeitarClienteUseCase.execute(cpf, request.motivo());
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/clientes/{cpf}")
    public ResponseEntity<ClienteResponse> alterarPerfil(
        @PathVariable String cpf,
        @Valid @RequestBody AlterarPerfilRequest request
    ) {
        ClienteResponse cliente = alterarPerfilUseCase.execute(cpf, request);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {
        clienteSeedService.reboot();
        return ResponseEntity.ok().build();
    }
}
