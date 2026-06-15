package com.ufpr.bantads.auth.application.dto.event;

public record UsuarioClienteRevertidoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email
) {}
