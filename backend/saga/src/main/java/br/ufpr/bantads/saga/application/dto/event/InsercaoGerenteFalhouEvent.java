package br.ufpr.bantads.saga.application.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InsercaoGerenteFalhouEvent(
    String sagaId,
    String motivo
) {
}
