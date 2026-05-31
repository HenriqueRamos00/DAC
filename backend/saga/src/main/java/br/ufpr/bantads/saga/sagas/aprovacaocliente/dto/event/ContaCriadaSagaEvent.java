package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContaCriadaSagaEvent(
    String sagaId,
    String numeroConta,
    LocalDateTime dataCriacao,
    BigDecimal saldo,
    BigDecimal limite,
    String clienteCpf,
    String clienteNome,
    String gerenteCpf,
    String gerenteNome,
    String gerenteEmail
) {}
