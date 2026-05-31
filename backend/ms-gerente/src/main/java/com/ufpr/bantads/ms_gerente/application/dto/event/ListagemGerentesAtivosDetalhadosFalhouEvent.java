package com.ufpr.bantads.ms_gerente.application.dto.event;

public record ListagemGerentesAtivosDetalhadosFalhouEvent(
    String sagaId,
    String motivo
) {}
