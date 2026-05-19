package com.ufpr.bantads.conta.application.dto.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AtribuirGerenteContaCommand(
    String sagaId,
    String gerenteOriginalCpf,
    String novoGerenteCpf
) {}