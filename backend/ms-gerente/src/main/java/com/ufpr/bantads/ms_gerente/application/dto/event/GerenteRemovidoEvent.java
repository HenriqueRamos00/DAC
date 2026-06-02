package com.ufpr.bantads.ms_gerente.application.dto.event;

public record GerenteRemovidoEvent(
    String sagaId,
    String cpf
) {}