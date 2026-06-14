package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command;

public record NotificarFalhaAutocadastroCommand(
    String sagaId,
    String cpf,
    String motivo
) {}
