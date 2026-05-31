package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

import java.util.List;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.shared.GerenteCandidato;

public record GerentesAtivosListadosEvent(
    String sagaId,
    List<GerenteCandidato> gerentes
) {}
