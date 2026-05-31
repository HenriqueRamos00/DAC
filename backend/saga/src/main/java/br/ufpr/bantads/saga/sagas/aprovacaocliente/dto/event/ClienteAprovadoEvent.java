package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClienteAprovadoEvent(
    String sagaId,
    String cpf,
    String email,
    String status,
    LocalDateTime dataAprovacao
) {}
