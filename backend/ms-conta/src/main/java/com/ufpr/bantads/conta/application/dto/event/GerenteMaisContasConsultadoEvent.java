package com.ufpr.bantads.conta.application.dto.event;

public record GerenteMaisContasConsultadoEvent(
    String sagaId,
    String cpf,
    Long totalContas
) {}