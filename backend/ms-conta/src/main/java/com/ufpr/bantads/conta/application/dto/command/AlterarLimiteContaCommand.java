package com.ufpr.bantads.conta.application.dto.command;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AlterarLimiteContaCommand(
    String sagaId,
    String cpf,
    String clienteNome,
    BigDecimal salario
) {}
