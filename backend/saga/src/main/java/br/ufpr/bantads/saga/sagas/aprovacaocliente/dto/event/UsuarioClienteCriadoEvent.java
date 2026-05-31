package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record UsuarioClienteCriadoEvent(
    String sagaId,
    String cpf,
    String email,
    String tipoUsuario,
    String senhaGerada
) {}
