package com.ufpr.bantads.conta.application.dto.response;

public record DepositoSaqueResponse(
    String conta,
    Double saldo,
    String data
) {}
