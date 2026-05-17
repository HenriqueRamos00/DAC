package com.ufpr.bantads.cliente.application.dto.command;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AlterarPerfilClienteCommand(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String telefone,
    BigDecimal salario,
    String cep,
    String logradouro,
    String cidade,
    String estado,
    String complemento,
    String numero
) {}
