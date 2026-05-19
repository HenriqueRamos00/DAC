package com.ufpr.bantads.auth.presentation.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufpr.bantads.auth.application.dto.request.CriarUsuarioRequest;
import com.ufpr.bantads.auth.application.dto.response.CriarUsuarioResponse;
import com.ufpr.bantads.auth.application.usecase.CriarUsuarioUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final CriarUsuarioUseCase criarUsuarioUseCase;

    @PostMapping
    public ResponseEntity<CriarUsuarioResponse> criar(
        @Valid @RequestBody CriarUsuarioRequest request
    ) {
        CriarUsuarioResponse response = criarUsuarioUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
