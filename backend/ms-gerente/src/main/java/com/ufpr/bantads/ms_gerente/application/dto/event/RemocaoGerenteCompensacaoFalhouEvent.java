package com.ufpr.bantads.ms_gerente.application.dto.event;

public record RemocaoGerenteCompensacaoFalhouEvent(
    String sagaId,
    String motivo
) {}