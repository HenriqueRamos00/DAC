package com.ufpr.bantads.conta.application.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContaCriadaEvent extends ContaEvent {

    private String sagaId;
    private Long contaId;
    private String numeroConta;
    private LocalDateTime dataCriacao;
    private BigDecimal saldo;
    private BigDecimal limite;
    private String clienteCpf;
    private String clienteNome;
    private String gerenteCpf;
    private String gerenteNome;
    private String gerenteEmail;
}
