package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record ExclusaoContaClienteFalhouEvent(
    String sagaId,
    String clienteCpf,
    String motivo
) {}
