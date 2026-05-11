package br.ufpr.bantads.saga.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConsultarGerenteMaisContasCommand(
    String sagaId
) {
}
