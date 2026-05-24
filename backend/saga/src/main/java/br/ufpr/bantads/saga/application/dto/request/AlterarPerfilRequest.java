package br.ufpr.bantads.saga.application.dto.request;

import java.math.BigDecimal;

public record AlterarPerfilRequest(
    String nome,
    String email,
    String telefone,
    BigDecimal salario,
    String endereco,
    String CEP,
    String cidade,
    String estado
) {}
