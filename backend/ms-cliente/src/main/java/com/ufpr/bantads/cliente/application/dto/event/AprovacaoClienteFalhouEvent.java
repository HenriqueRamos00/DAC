package com.ufpr.bantads.cliente.application.dto.event;

public record AprovacaoClienteFalhouEvent(
    String cpf,
    String motivo
) {
}
