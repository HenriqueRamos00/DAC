package com.ufpr.bantads.auth.application.dto.event;

public record CriacaoUsuarioGerenteFalhouEvent(
    String sagaId,
    String motivo
) {}