package com.ufpr.bantads.cliente.application.dto.event;

public record ClienteAlteracaoFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
