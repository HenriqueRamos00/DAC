package com.ufpr.bantads.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.ufpr.bantads.auth.domain.model.enums.TipoUsuario;

public record CriarUsuarioRequest(
    @NotBlank String cpf,
    @NotBlank @Email String email,
    String senha,
    @NotNull TipoUsuario tipoUsuario
) {
}
