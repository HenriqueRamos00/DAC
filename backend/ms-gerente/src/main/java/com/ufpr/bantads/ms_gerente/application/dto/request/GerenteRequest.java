package com.ufpr.bantads.ms_gerente.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GerenteRequest(
    String cpf,
    String nome,
    String email,
    String telefone,
    String senha,
    String tipo
) {
}
