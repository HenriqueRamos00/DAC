package com.ufpr.bantads.auth.presentation.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufpr.bantads.auth.application.dto.request.CriarUsuarioRequest;
import com.ufpr.bantads.auth.application.dto.response.CriarUsuarioResponse;
import com.ufpr.bantads.auth.application.usecase.CriarUsuarioUseCase;
import com.ufpr.bantads.auth.infrastructure.config.AuthDatabaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final AuthDatabaseService authDatabaseService;

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {
        authDatabaseService.reboot();
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<CriarUsuarioResponse> criar(
        @Valid @RequestBody CriarUsuarioRequest request
    ) {
        CriarUsuarioResponse response = criarUsuarioUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
