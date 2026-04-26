package com.ufpr.bantads.cliente.application.dto.event;

public record RejeicaoClienteFalhouEvent(
    String cpf,
    String motivo
) {
}
