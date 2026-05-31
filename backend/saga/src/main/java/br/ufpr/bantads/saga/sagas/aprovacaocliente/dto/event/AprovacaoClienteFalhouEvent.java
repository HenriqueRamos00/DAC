package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record AprovacaoClienteFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
