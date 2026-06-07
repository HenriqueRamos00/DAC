package com.ufpr.bantads.auth.application.dto.command;

public record ExcluirUsuarioClienteCommand(
    String sagaId,
    String cpf,
    String email
) {}
