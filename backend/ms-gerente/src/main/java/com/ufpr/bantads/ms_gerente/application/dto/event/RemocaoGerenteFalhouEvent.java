package com.ufpr.bantads.ms_gerente.application.dto.event;

public record RemocaoGerenteFalhouEvent(
    String sagaId,
    String motivo
) {}