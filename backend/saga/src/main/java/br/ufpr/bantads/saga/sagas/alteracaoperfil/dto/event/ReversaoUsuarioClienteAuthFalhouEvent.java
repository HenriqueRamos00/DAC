package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event;

public record ReversaoUsuarioClienteAuthFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
