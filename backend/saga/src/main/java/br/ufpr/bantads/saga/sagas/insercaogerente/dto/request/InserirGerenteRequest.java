package br.ufpr.bantads.saga.sagas.insercaogerente.dto.request;

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
