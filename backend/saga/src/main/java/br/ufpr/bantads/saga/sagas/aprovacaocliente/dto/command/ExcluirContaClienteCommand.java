package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command;

public record ExcluirContaClienteCommand(
    String sagaId,
    String clienteCpf,
    String numeroConta
) {}
