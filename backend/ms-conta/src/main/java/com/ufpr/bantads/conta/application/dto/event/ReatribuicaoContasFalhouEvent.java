package com.ufpr.bantads.conta.application.dto.event;

public record ReatribuicaoContasFalhouEvent(
    String sagaId,
    String motivo
) {}