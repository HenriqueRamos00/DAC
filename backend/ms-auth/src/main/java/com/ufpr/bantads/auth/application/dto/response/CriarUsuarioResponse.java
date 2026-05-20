package com.ufpr.bantads.auth.application.dto.response;

import com.ufpr.bantads.auth.domain.model.enums.TipoUsuario;

public record CriarUsuarioResponse(
    String cpf,
    String email,
    TipoUsuario tipoUsuario,
    String senhaGerada
) {
}
