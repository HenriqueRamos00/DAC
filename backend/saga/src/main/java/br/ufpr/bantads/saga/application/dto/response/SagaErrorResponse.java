package br.ufpr.bantads.saga.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SagaErrorResponse(
    String sagaId,
    String status,
    String motivo
) {}
