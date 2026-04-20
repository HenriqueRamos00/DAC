package com.ufpr.bantads.conta.application.dto.event;

import com.ufpr.bantads.conta.domain.model.enums.TipoMovimentacao;

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
public class MovimentacaoEvent extends ContaEvent {

    private Long movimentacaoId;
    private TipoMovimentacao tipo;
    private BigDecimal valor;
    private LocalDateTime dataHora;

    private String numeroContaOrigem;
    private BigDecimal novoSaldoContaOrigem;

    private String numeroContaDestino;
    private BigDecimal novoSaldoContaDestino;

}
