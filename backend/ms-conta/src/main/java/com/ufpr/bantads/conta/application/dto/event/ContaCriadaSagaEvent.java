package com.ufpr.bantads.conta.application.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * utilizada para tirar a ambiguidade do listener dos
 * dois eventos de criação de conta: cqrs e saga
 * */
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
) {
    public static ContaCriadaSagaEvent from(ContaCriadaEvent event) {
        return new ContaCriadaSagaEvent(
            event.getSagaId(),
            event.getNumeroConta(),
            event.getDataCriacao(),
            event.getSaldo(),
            event.getLimite(),
            event.getClienteCpf(),
            event.getClienteNome(),
            event.getGerenteCpf(),
            event.getGerenteNome(),
            event.getGerenteEmail()
        );
    }
}
