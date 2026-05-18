package com.ufpr.bantads.conta.application.dto.event;

import java.math.BigDecimal;

public record ContaLimiteAlteradoEvent(
    String sagaId,
    String cpf,
    String numeroConta,
    BigDecimal saldo,
    BigDecimal limite,
    String gerenteCpf,
    String gerenteNome
) {}
