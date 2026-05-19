package com.ufpr.bantads.conta.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConsultarGerenteMaisContasCommand(
    String sagaId
) {}