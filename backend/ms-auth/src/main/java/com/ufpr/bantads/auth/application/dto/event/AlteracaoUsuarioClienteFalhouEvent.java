package com.ufpr.bantads.auth.application.dto.event;

public record AlteracaoUsuarioClienteFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
