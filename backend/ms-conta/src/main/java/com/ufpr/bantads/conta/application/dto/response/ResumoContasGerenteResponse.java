package com.ufpr.bantads.conta.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ResumoContasGerenteResponse(
    String gerenteCpf,
    List<ContaResponse> clientes,
    BigDecimal saldoPositivo,
    BigDecimal saldoNegativo
) {
}
