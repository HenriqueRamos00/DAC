package com.ufpr.bantads.auth.application.dto.event;

import com.ufpr.bantads.auth.application.dto.response.CriarUsuarioResponse;

public record UsuarioClienteCriadoEvent(
    String sagaId,
    String cpf,
    String email,
    String tipoUsuario,
    String senhaGerada
) {
    public static UsuarioClienteCriadoEvent fromResponse(String sagaId, CriarUsuarioResponse response) {
        return new UsuarioClienteCriadoEvent(
            sagaId,
            response.cpf(),
            response.email(),
            response.tipoUsuario().name(),
            response.senhaGerada()
        );
    }
}
