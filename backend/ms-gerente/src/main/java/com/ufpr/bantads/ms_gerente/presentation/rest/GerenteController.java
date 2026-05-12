package com.ufpr.bantads.ms_gerente.presentation.rest;

import org.springframework.web.bind.annotation.RestController;

import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;
import com.ufpr.bantads.ms_gerente.application.usecase.GetGerenteByCpfUseCase;
import com.ufpr.bantads.ms_gerente.application.usecase.ListAllGerentesUseCase;
import com.ufpr.bantads.ms_gerente.domain.exception.FiltroInvalidoException;
import com.ufpr.bantads.ms_gerente.domain.exception.UsuarioNaoAutenticadoException;
import com.ufpr.bantads.ms_gerente.domain.exception.UsuarioSemPermissaoException;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequiredArgsConstructor
public class GerenteController {

    private final ListAllGerentesUseCase listAllGerentesUseCase;
    private final GetGerenteByCpfUseCase getGerenteByCpfUseCase;

    @GetMapping("/gerentes")
    public ResponseEntity<List<GerenteResponse>> getAllGerentes(
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        autenticar(userId, userRole);
        if (!"ADMIN".equals(userRole)) {
            throw new UsuarioSemPermissaoException();
        }

        List<GerenteResponse> gerentes = listAllGerentesUseCase.execute();
        return ResponseEntity.ok(gerentes);
    }


    @GetMapping("/gerentes/{cpf}")
    public ResponseEntity<GerenteResponse> getGerenteByCpf(
        @PathVariable String cpf,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        if (cpf == null || cpf.isBlank()) {
            throw new FiltroInvalidoException("Filtro não existe");
        }

        autenticar(userId, userRole);
        autorizarConsultaPorCpf(cpf, userId, userRole);

        GerenteResponse gerente = getGerenteByCpfUseCase.execute(cpf);
        return ResponseEntity.ok(gerente);
    }

    private void autenticar(String userId, String userRole) {
        if (userId == null || userId.isBlank() || userRole == null || userRole.isBlank()) {
            throw new UsuarioNaoAutenticadoException();
        }
    }

    private void autorizarConsultaPorCpf(String cpf, String userId, String userRole) {
        switch (userRole) {
            case "ADMIN":
                return;
            case "GERENTE":
                if (!userId.equals(cpf)) {
                    throw new UsuarioSemPermissaoException();
                }
                return;
            default:
                throw new UsuarioSemPermissaoException();
        }
    }

}
