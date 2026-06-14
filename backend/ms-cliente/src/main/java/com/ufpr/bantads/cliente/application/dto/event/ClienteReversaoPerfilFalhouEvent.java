package com.ufpr.bantads.cliente.application.dto.event;

public record ClienteReversaoPerfilFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
