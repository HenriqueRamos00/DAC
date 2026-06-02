package com.ufpr.bantads.conta.application.dto.event;

public record ContasReatribuidasEvent(
    String sagaId,
    String gerenteDestinoCpf,
    Long contasReatribuidas
) {}