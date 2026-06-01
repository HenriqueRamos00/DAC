package com.ufpr.bantads.cliente.application.dto.event;

public record AprovacaoClienteFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {
}
