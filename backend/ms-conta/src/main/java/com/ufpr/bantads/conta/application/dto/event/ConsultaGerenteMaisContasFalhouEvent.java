package com.ufpr.bantads.conta.application.dto.event;

public record ConsultaGerenteMaisContasFalhouEvent(
    String sagaId,
    String motivo
) {}