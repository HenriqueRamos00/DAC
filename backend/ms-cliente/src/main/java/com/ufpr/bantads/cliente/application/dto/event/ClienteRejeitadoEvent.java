package com.ufpr.bantads.cliente.application.dto.event;

import com.ufpr.bantads.cliente.domain.model.Cliente;
import java.time.LocalDateTime;

public record ClienteRejeitadoEvent(
    Long codigo,
    String cpf,
    String email,
    String status,
    String motivoRejeicao,
    LocalDateTime dataReprovacao
) {
    public static ClienteRejeitadoEvent fromEntity(Cliente cliente) {
        return new ClienteRejeitadoEvent(
            cliente.getId(),
            cliente.getCpf(),
            cliente.getEmail(),
            cliente.getStatus().name(),
            cliente.getMotivoRejeicao(),
            cliente.getDataReprovacao()
        );
    }
}
