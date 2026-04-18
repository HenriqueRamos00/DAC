package com.ufpr.bantads.cliente.presentation.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufpr.bantads.cliente.application.dto.response.ClienteParaAprovarResponse;
import com.ufpr.bantads.cliente.application.dto.response.ClienteResponse;
import com.ufpr.bantads.cliente.application.usecase.GetClienteByCpfUseCase;
import com.ufpr.bantads.cliente.application.usecase.ListAllClientesUseCase;
import com.ufpr.bantads.cliente.application.usecase.ListClientesParaAprovarUseCase;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ClienteController {

    private final ListAllClientesUseCase listAllClientesUseCase;
    private final ListClientesParaAprovarUseCase listClientesParaAprovarUseCase;
    private final GetClienteByCpfUseCase getClienteByCpfUseCase;

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
