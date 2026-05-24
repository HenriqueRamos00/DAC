package br.ufpr.bantads.saga.application.dto.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaLimiteAlteradoEvent {
    private String sagaId;
    private String cpf;
    private String numeroConta;
    private BigDecimal saldo;
    private BigDecimal limite;
    private String gerenteCpf;

}
