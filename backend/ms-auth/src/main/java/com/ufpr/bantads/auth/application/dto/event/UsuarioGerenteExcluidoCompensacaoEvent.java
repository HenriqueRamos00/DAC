package com.ufpr.bantads.auth.application.dto.event;

public record UsuarioGerenteExcluidoCompensacaoEvent(
    String sagaId,
    String cpf,
    String email
) {}