package com.ufpr.bantads.auth.application.dto.event;

public record CriacaoUsuarioClienteFalhouEvent(
    String sagaId,
    String motivo
) {}
