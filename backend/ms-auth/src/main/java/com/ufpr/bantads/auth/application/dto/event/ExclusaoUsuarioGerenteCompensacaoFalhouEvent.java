package com.ufpr.bantads.auth.application.dto.event;

public record ExclusaoUsuarioGerenteCompensacaoFalhouEvent(
    String sagaId,
    String motivo
) {}