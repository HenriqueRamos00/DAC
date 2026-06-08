package com.ufpr.bantads.conta.application.dto.response;

import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;

public record ContaResponse(
    String numeroConta,
    String dataCriacao,
    Double saldo,
    Double limite,
    String clienteNome,
    String clienteCpf,
    String gerenteCpf,
    String gerenteNome,
    String gerenteEmail
) {
    public static ContaResponse fromEntity(ContaQuery contaQuery) {
        return new ContaResponse(
            contaQuery.getNumeroConta(),
            contaQuery.getDataCriacao().toString(),
            contaQuery.getSaldo().doubleValue(),
            contaQuery.getLimite().doubleValue(),
            contaQuery.getClienteNome(),
            contaQuery.getClienteCpf(),
            contaQuery.getGerenteCpf(),
            contaQuery.getGerenteNome(),
            contaQuery.getGerenteEmail()
        );
    }

    public static ContaResponse fromEntity(ContaQuery contaQuery , double saldo) {
                
        return new ContaResponse(
            contaQuery.getNumeroConta(),
            contaQuery.getDataCriacao().toString(),
            saldo,
            contaQuery.getLimite().doubleValue(),
            contaQuery.getClienteNome(),
            contaQuery.getClienteCpf(),
            contaQuery.getGerenteCpf(),
            contaQuery.getGerenteNome(),
            contaQuery.getGerenteEmail()
        );
    }
}