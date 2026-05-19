package com.ufpr.bantads.conta.application.dto.event;

public record GerenteAtribuidoContaEvent(
    String sagaId,
    Long contasReatribuidas
) {}