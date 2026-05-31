package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record SelecaoGerenteParaNovaContaFalhouEvent(
    String sagaId,
    String motivo
) {}
