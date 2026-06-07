package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record UsuarioClienteExcluidoEvent(
    String sagaId,
    String cpf,
    String email
) {}
