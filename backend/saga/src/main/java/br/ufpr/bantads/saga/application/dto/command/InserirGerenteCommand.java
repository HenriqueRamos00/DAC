package br.ufpr.bantads.saga.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InserirGerenteCommand(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String senha,
    String tipo
) {
}
