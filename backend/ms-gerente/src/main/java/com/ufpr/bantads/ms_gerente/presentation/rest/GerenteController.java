package com.ufpr.bantads.ms_gerente.presentation.rest;

import org.springframework.web.bind.annotation.RestController;

import com.ufpr.bantads.ms_gerente.application.dto.request.GerenteRequest;
import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;
import com.ufpr.bantads.ms_gerente.application.usecase.CreateGerenteUseCase;
import com.ufpr.bantads.ms_gerente.application.usecase.DeleteGerenteUseCase;
import com.ufpr.bantads.ms_gerente.application.usecase.GetGerenteByCpfUseCase;
import com.ufpr.bantads.ms_gerente.application.usecase.ListAllGerentesUseCase;
import com.ufpr.bantads.ms_gerente.application.usecase.UpdateGerenteUseCase;
import com.ufpr.bantads.ms_gerente.domain.exception.FiltroInvalidoException;
import com.ufpr.bantads.ms_gerente.domain.exception.UsuarioNaoAutenticadoException;
import com.ufpr.bantads.ms_gerente.domain.exception.UsuarioSemPermissaoException;
import com.ufpr.bantads.ms_gerente.infrastructure.config.GerenteSeedService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequiredArgsConstructor
public class GerenteController {

    private final ListAllGerentesUseCase listAllGerentesUseCase;
    private final GetGerenteByCpfUseCase getGerenteByCpfUseCase;
    private final CreateGerenteUseCase createGerenteUseCase;
    private final UpdateGerenteUseCase updateGerenteUseCase;
    private final DeleteGerenteUseCase deleteGerenteUseCase;
    private final GerenteSeedService gerenteSeedService;

    @GetMapping("/gerentes")
    public ResponseEntity<List<GerenteResponse>> getAllGerentes(
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        autenticar(userId, userRole);
        autorizarAdmin(userRole);

        List<GerenteResponse> gerentes = listAllGerentesUseCase.execute();
        return ResponseEntity.ok(gerentes);
    }


    @GetMapping("/gerentes/{cpf}")
    public ResponseEntity<GerenteResponse> getGerenteByCpf(
        @PathVariable String cpf,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        validarCpf(cpf);

        autenticar(userId, userRole);
        autorizarAdmin(userRole);

        GerenteResponse gerente = getGerenteByCpfUseCase.execute(cpf);
        return ResponseEntity.ok(gerente);
    }

    @PostMapping("/gerentes")
    public ResponseEntity<GerenteResponse> createGerente(
        @RequestBody GerenteRequest request,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        autenticar(userId, userRole);
        autorizarAdmin(userRole);

        GerenteResponse gerente = createGerenteUseCase.execute(request);
        return ResponseEntity.status(201).body(gerente);
    }

    @PutMapping("/gerentes/{cpf}")
    public ResponseEntity<GerenteResponse> updateGerente(
        @PathVariable String cpf,
        @RequestBody GerenteRequest request,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        validarCpf(cpf);

        autenticar(userId, userRole);
        autorizarAdmin(userRole);

        GerenteResponse gerente = updateGerenteUseCase.execute(cpf, request);
        return ResponseEntity.ok(gerente);
    }

    @DeleteMapping("/gerentes/{cpf}")
    public ResponseEntity<Void> deleteGerente(
        @PathVariable String cpf,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        validarCpf(cpf);

        autenticar(userId, userRole);
        autorizarAdmin(userRole);

        deleteGerenteUseCase.execute(cpf);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {
        gerenteSeedService.reboot();
        return ResponseEntity.ok().build();
    }

    private void autenticar(String userId, String userRole) {
        if (userId == null || userId.isBlank() || userRole == null || userRole.isBlank()) {
            throw new UsuarioNaoAutenticadoException();
        }
    }

    private void validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new FiltroInvalidoException("CPF do gerente é obrigatório");
        }
    }

    private void autorizarAdmin(String userRole) {
        if (!"ADMINISTRADOR".equals(userRole)) {
            throw new UsuarioSemPermissaoException();
        }
    }

}
