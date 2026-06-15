package com.ufpr.bantads.auth.application.dto.event;

public record UsuarioClienteAlteradoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String nomeAnterior,
    String emailAnterior
) {}
