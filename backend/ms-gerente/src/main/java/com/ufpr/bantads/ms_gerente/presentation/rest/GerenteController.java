package com.ufpr.bantads.ms_gerente.presentation.rest;

import org.springframework.web.bind.annotation.RestController;

import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;
import com.ufpr.bantads.ms_gerente.application.usecase.GetGerenteByCpfUseCase;
import com.ufpr.bantads.ms_gerente.application.usecase.ListAllGerentesUseCase;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
public class GerenteController {
    
    private final ListAllGerentesUseCase listAllGerentesUseCase;
    private final GetGerenteByCpfUseCase getGerenteByCpfUseCase;

    @GetMapping("/gerentes")
    public ResponseEntity<List<GerenteResponse>> getAllGerentes() {
        List<GerenteResponse> gerentes = listAllGerentesUseCase.execute();
        return ResponseEntity.ok(gerentes);
    }
    

    @GetMapping("/gerentes/{cpf}")
    public ResponseEntity<GerenteResponse> getGerenteByCpf(@PathVariable String cpf) {
        GerenteResponse gerente = getGerenteByCpfUseCase.execute(cpf);
        return ResponseEntity.ok(gerente);
    }

}
