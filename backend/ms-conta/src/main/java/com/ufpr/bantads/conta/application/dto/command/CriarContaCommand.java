package com.ufpr.bantads.conta.application.dto.command;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CriarContaCommand(
    String sagaId,
    String clienteCpf,
    String clienteNome,
    BigDecimal salario,
    String gerenteCpf,
    String gerenteNome,
    String gerenteEmail
) {}
