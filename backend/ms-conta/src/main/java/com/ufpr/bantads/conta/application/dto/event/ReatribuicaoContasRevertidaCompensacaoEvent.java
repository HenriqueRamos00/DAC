package com.ufpr.bantads.conta.application.dto.event;

public record ReatribuicaoContasRevertidaCompensacaoEvent(
    String sagaId,
    Long contasRevertidas
) {}