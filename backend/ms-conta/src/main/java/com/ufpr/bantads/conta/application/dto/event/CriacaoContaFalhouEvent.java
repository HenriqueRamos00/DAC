package com.ufpr.bantads.conta.application.dto.event;

public record CriacaoContaFalhouEvent(
    String sagaId,
    String clienteCpf,
    String motivo
) {}
