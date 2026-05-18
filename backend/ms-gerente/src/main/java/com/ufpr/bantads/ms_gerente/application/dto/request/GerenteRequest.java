package com.ufpr.bantads.ms_gerente.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ufpr.bantads.ms_gerente.application.dto.command.InserirGerenteCommand;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GerenteRequest(
    String cpf,
    String nome,
    String email,
    String telefone,
    String senha,
    String tipo
) {

    public static GerenteRequest fromCommand(InserirGerenteCommand command) {
        return new GerenteRequest(
            command.cpf(),
            command.nome(),
            command.email(),
            null,
            command.senha(),
            command.tipo()
        );
    }
}