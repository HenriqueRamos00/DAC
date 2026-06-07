package com.ufpr.bantads.conta.application.dto.event;

public record ExclusaoContaClienteFalhouEvent(
    String sagaId,
    String clienteCpf,
    String motivo
) {}
