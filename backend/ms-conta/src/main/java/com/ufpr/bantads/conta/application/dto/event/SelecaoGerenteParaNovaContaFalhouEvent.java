package com.ufpr.bantads.conta.application.dto.event;

public record SelecaoGerenteParaNovaContaFalhouEvent(
    String sagaId,
    String motivo
) {}
