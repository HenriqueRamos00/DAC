package com.ufpr.bantads.ms_gerente.application.dto.event;

import java.util.List;

public record GerentesAtivosDetalhadosListadosEvent(
    String sagaId,
    List<GerenteAtivoDetalhado> gerentes
) {}
