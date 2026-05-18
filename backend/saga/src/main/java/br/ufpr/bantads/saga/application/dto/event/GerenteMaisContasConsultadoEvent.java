package br.ufpr.bantads.saga.application.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GerenteMaisContasConsultadoEvent(
    String sagaId,
    String cpf,
    Long totalContas
) {
}
