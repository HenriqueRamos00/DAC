package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record CriacaoUsuarioClienteFalhouEvent(
    String sagaId,
    String motivo
) {}
