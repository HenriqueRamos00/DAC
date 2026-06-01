package com.ufpr.bantads.conta.application.dto.command;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ufpr.bantads.conta.application.dto.shared.GerenteCandidato;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SelecionarGerenteParaNovaContaCommand(
    String sagaId,
    List<GerenteCandidato> gerentes
) {}
