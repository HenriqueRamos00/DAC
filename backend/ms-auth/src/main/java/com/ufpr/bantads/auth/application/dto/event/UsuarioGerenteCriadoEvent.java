package com.ufpr.bantads.auth.application.dto.event;

import com.ufpr.bantads.auth.application.dto.response.CriarUsuarioResponse;

public record UsuarioGerenteCriadoEvent(
    String sagaId,
    String cpf,
    String email
) {

    public static UsuarioGerenteCriadoEvent fromResponse(String sagaId, CriarUsuarioResponse response) {
        return new UsuarioGerenteCriadoEvent(
            sagaId,
            response.cpf(),
            response.email()
        );
    }
}