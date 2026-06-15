package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event;

public record UsuarioClienteAuthAlteradoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String nomeAnterior,
    String emailAnterior
) {}
