package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record ContaClienteExcluidaEvent(
    String sagaId,
    String clienteCpf,
    String numeroConta
) {}
