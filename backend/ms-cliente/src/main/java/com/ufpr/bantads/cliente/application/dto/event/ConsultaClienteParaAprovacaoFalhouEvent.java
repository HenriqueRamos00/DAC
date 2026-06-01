package com.ufpr.bantads.cliente.application.dto.event;

public record ConsultaClienteParaAprovacaoFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
