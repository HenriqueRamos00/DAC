package com.ufpr.bantads.cliente.application.dto.command;

public record ConsultarClienteParaAprovacaoCommand(
    String sagaId,
    String cpf
) {}
