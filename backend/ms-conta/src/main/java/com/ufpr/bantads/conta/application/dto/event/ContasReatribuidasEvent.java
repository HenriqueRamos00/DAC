package com.ufpr.bantads.conta.application.dto.event;

import java.util.List;

public record ContasReatribuidasEvent(
    String sagaId,
    String gerenteDestinoCpf,
    Long contasReatribuidas,
    List<String> numerosContasMovidas
) {}