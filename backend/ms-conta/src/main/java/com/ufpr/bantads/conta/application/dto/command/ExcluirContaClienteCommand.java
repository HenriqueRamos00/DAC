package com.ufpr.bantads.conta.application.dto.command;

public record ExcluirContaClienteCommand(
    String sagaId,
    String clienteCpf,
    String numeroConta
) {}
