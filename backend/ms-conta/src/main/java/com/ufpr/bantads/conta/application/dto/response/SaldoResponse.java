package com.ufpr.bantads.conta.application.dto.response;

import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;

public record SaldoResponse(
    String cliente,
    String conta,
    Double saldo
) {
    public static SaldoResponse fromEntity(ContaQuery contaQuery) {
        return new SaldoResponse(
            contaQuery.getClienteCpf(), 
            contaQuery.getNumeroConta(), 
            contaQuery.getSaldo().doubleValue()
        );
    }
}
