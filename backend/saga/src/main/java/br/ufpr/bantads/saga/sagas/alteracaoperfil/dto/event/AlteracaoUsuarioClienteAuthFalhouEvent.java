package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event;

public record AlteracaoUsuarioClienteAuthFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
