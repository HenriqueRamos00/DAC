package com.ufpr.bantads.conta.application.dto.event;

public record AtribuicaoGerenteContaFalhouEvent(
    String sagaId,
    String motivo
) {}