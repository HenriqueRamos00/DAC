package com.ufpr.bantads.auth.application.dto.event;

public record ReversaoUsuarioClienteFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
