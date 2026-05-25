package com.ufpr.bantads.conta.application.dto.request;

import java.math.BigDecimal;

import com.ufpr.bantads.conta.application.dto.command.CriarContaCommand;

public record CriarContaRequest(
    String sagaId,
    String clienteCpf,
    String clienteNome,
    BigDecimal salario,
    String gerenteCpf,
    String gerenteNome,
    String gerenteEmail
) {
    public CriarContaCommand toCommand() {
        return new CriarContaCommand(
            sagaId,
            clienteCpf,
            clienteNome,
            salario,
            gerenteCpf,
            gerenteNome,
            gerenteEmail
        );
    }
}
