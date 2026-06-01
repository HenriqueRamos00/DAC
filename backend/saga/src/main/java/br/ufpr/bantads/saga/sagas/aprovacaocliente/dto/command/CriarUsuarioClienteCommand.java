package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command;

public record CriarUsuarioClienteCommand(
    String sagaId,
    String cpf,
    String email
) {}
