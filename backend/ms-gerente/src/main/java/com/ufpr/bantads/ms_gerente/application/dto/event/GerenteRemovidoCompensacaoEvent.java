package com.ufpr.bantads.ms_gerente.application.dto.event;

public record GerenteRemovidoCompensacaoEvent(
    String sagaId,
    String cpf
) {}