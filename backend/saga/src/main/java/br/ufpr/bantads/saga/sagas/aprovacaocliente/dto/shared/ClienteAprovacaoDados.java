package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.shared;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClienteAprovacaoDados(
    String cpf,
    String nome,
    String email,
    BigDecimal salario
) {}
