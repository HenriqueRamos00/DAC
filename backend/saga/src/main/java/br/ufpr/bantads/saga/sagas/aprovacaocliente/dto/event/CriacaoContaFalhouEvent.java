package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record CriacaoContaFalhouEvent(
    String sagaId,
    String clienteCpf,
    String motivo
) {}
