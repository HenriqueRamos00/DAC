package com.ufpr.bantads.ms_gerente.application.dto.event;

import java.util.List;

public record GerentesAtivosListadosEvent(
    String sagaId,
    List<String> cpfs
) {}