package br.ufpr.bantads.saga.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AtribuirGerenteContaCommand(
    String sagaId,
    Long gerenteOriginalId,
    Long novoGerenteId
) {
}
