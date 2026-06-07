package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record ExclusaoUsuarioClienteFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
