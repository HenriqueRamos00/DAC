package com.ufpr.bantads.ms_gerente.application.dto.event;

public record ListagemGerentesAtivosFalhouEvent(
    String sagaId,
    String motivo
) {}