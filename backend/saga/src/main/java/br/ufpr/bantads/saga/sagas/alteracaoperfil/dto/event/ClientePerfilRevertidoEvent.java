package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event;

public record ClientePerfilRevertidoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email
) {}
