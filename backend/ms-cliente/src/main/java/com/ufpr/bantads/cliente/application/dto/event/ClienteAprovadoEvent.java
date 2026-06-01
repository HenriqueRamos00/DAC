package com.ufpr.bantads.cliente.application.dto.event;

import com.ufpr.bantads.cliente.domain.model.Cliente;
import java.time.LocalDateTime;

public record ClienteAprovadoEvent(
    String sagaId,
    Long codigo,
    String cpf,
    String email,
    String status,
    LocalDateTime dataAprovacao
) {
    public static ClienteAprovadoEvent fromEntity(Cliente cliente) {
        return fromEntity(null, cliente);
    }

    public static ClienteAprovadoEvent fromEntity(String sagaId, Cliente cliente) {
        return new ClienteAprovadoEvent(
            sagaId,
            cliente.getId(),
            cliente.getCpf(),
            cliente.getEmail(),
            cliente.getStatus().name(),
            cliente.getDataAprovacao()
        );
    }
}
