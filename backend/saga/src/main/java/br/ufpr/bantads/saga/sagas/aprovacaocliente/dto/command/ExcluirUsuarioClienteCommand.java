package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command;

public record ExcluirUsuarioClienteCommand(
    String sagaId,
    String cpf,
    String email
) {}
