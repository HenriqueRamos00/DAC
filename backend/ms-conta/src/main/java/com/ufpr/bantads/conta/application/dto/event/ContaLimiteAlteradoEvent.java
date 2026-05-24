package com.ufpr.bantads.conta.application.dto.event;

import java.math.BigDecimal;

import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;

public record ContaLimiteAlteradoEvent(
    String sagaId,
    String cpf,
    String numeroConta,
    BigDecimal saldo,
    BigDecimal limite,
    String gerenteCpf
) {
    public static ContaLimiteAlteradoEvent fromEntity(String sagaId, ContaCommand conta) {
        return new ContaLimiteAlteradoEvent(
            sagaId,
            conta.getClienteCpf(),
            conta.getNumeroConta(),
            conta.getSaldo(),
            conta.getLimite(),
            conta.getGerenteCpf()
        );
    }
}
