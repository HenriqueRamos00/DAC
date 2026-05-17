package br.ufpr.bantads.saga.application.dto.event;

public record ClienteAlteracaoFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
