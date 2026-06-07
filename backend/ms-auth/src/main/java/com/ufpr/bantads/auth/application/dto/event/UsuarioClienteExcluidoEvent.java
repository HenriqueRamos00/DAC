package com.ufpr.bantads.auth.application.dto.event;

public record UsuarioClienteExcluidoEvent(
    String sagaId,
    String cpf,
    String email
) {}
