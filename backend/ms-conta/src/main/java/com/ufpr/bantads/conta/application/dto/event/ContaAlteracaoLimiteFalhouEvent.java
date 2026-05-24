package com.ufpr.bantads.conta.application.dto.event;

public record ContaAlteracaoLimiteFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
