package com.ufpr.bantads.conta.application.dto.response;

import java.util.List;

public record ExtratoResponse(
    String conta,
    Double saldo,
    List<Extrato> movimentacoes
) {
    
}
