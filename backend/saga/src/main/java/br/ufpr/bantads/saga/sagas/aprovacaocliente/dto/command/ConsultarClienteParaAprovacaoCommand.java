package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command;

public record ConsultarClienteParaAprovacaoCommand(
    String sagaId,
    String cpf
) {}
