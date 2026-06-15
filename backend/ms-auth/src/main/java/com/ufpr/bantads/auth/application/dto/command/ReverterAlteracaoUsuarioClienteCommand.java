package com.ufpr.bantads.auth.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReverterAlteracaoUsuarioClienteCommand(
    String sagaId,
    String cpf,
    String nome,
    String email
) {}
