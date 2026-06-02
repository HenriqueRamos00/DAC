package com.ufpr.bantads.conta.application.dto.command;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReatribuirContasGerenteCommand(
    String sagaId,
    String cpfOrigem,
    List<String> candidatosDestino
) {}