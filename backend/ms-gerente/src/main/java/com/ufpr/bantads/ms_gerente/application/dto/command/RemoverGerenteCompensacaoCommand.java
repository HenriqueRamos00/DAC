package com.ufpr.bantads.ms_gerente.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoverGerenteCompensacaoCommand(
    String sagaId,
    String cpf
) {}