package com.ufpr.bantads.cliente.application.dto.event;

import com.ufpr.bantads.cliente.domain.model.Cliente;

public record ClientePerfilRevertidoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email
) {
    public static ClientePerfilRevertidoEvent fromEntity(String sagaId, Cliente cliente) {
        return new ClientePerfilRevertidoEvent(
            sagaId,
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getEmail()
        );
    }
}
