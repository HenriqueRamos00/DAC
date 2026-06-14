package com.ufpr.bantads.conta.application.dto.event;

public record ReversaoReatribuicaoContasCompensacaoFalhouEvent(
    String sagaId,
    String motivo
) {}