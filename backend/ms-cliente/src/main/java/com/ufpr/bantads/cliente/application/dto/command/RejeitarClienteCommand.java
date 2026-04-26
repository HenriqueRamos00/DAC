package com.ufpr.bantads.cliente.application.dto.command;

public record RejeitarClienteCommand(
    String cpf,
    String motivo
) {
}
