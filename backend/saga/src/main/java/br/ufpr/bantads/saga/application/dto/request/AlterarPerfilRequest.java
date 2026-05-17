package br.ufpr.bantads.saga.application.dto.request;

import java.math.BigDecimal;

public record AlterarPerfilRequest(
    String nome,
    String email,
    BigDecimal salario,
    String endereco,
    String cep,
    String cidade,
    String estado
) {}