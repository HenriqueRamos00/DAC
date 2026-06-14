package com.ufpr.bantads.cliente.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificarFalhaAutocadastroCommand(
    String sagaId,
    String cpf,
    String motivo
) {}
