package br.ufpr.bantads.saga.application.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GerenteInseridoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String tipo
) {
}
