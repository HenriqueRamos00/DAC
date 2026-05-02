package com.ufpr.bantads.auth.presentation.rest;

import org.springframework.web.bind.annotation.RestController;

import com.ufpr.bantads.auth.application.dto.request.LoginRequest;
import com.ufpr.bantads.auth.application.dto.response.UsuarioResponse;
import com.ufpr.bantads.auth.application.usecase.LoginUseCase;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@RequestBody LoginRequest request) {
        
        var usuario = loginUseCase.execute(request.login(), request.senha());
        return ResponseEntity.ok(UsuarioResponse.fromEntity(usuario));

    }
    

}
