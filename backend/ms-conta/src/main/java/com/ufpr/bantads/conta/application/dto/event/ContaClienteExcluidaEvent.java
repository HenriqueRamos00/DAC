package com.ufpr.bantads.conta.application.dto.event;

public record ContaClienteExcluidaEvent(
    String sagaId,
    String clienteCpf,
    String numeroConta
) {}
