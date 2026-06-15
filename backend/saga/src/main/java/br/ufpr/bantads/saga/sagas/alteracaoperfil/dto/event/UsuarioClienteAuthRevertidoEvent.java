package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event;

public record UsuarioClienteAuthRevertidoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email
) {}
