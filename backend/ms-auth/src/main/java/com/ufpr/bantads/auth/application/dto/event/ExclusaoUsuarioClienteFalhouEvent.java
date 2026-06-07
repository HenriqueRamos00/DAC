package com.ufpr.bantads.auth.application.dto.event;

public record ExclusaoUsuarioClienteFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
