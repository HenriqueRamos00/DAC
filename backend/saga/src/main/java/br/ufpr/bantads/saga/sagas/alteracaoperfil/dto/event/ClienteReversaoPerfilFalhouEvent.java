package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event;

public record ClienteReversaoPerfilFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
