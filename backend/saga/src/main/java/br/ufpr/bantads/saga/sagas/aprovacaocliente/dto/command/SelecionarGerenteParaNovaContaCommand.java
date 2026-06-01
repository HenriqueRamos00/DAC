package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command;

import java.util.List;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.shared.GerenteCandidato;

public record SelecionarGerenteParaNovaContaCommand(
    String sagaId,
    List<GerenteCandidato> gerentes
) {}
