package com.ufpr.bantads.auth.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExcluirUsuarioGerenteCompensacaoCommand(
    String sagaId,
    String cpf,
    String email
) {}