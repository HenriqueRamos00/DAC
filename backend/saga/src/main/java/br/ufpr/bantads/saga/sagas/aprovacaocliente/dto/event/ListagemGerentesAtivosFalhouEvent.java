package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record ListagemGerentesAtivosFalhouEvent(
    String sagaId,
    String motivo
) {}
