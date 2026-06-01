package com.ufpr.bantads.cliente.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AprovarClienteCommand(
    String sagaId,
    String cpf,
    String senhaGerada
) {
}
