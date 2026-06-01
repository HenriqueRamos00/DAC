package com.ufpr.bantads.cliente.application.dto.event;

import java.math.BigDecimal;

import com.ufpr.bantads.cliente.domain.model.Cliente;

public record ClienteConsultadoParaAprovacaoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email,
    BigDecimal salario
) {
    public static ClienteConsultadoParaAprovacaoEvent fromEntity(String sagaId, Cliente cliente) {
        return new ClienteConsultadoParaAprovacaoEvent(
            sagaId,
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getSalario()
        );
    }
}
