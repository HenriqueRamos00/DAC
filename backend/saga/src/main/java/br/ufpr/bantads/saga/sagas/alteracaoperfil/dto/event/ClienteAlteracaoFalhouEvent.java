package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event;
public record ClienteAlteracaoFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
