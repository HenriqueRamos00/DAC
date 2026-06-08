package com.ufpr.bantads.conta.application.dto.response;

public record TransferenciaResponse(
    String conta,
    String destino,
    Double valor,
    Double saldo,
    String data
) {}
