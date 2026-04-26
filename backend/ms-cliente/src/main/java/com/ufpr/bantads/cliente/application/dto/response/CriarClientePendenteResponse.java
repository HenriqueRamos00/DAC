package com.ufpr.bantads.cliente.application.dto.response;

import com.ufpr.bantads.cliente.domain.model.Cliente;

public record CriarClientePendenteResponse(
    Long codigo,
    String cpf,
    String email
) {
    public static CriarClientePendenteResponse fromEntity(Cliente cliente) {
        return new CriarClientePendenteResponse(
            cliente.getId(),
            cliente.getCpf(),
            cliente.getEmail()
        );
    }
}
