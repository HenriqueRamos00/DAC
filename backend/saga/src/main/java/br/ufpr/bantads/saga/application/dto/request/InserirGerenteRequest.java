package br.ufpr.bantads.saga.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InserirGerenteRequest(
    String cpf,
    String nome,
    String email,
    String senha,
    String tipo
) {
}
