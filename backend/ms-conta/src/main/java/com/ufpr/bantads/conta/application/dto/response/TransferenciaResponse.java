package com.ufpr.bantads.conta.application.dto.response;

public record TransferenciaResponse(
    String conta,
    Double saldo,
    String data
) {}
