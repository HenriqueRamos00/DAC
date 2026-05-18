package com.ufpr.bantads.ms_gerente.application.dto.event;

public record InsercaoGerenteFalhouEvent(
    String sagaId,
    String motivo
) {}
