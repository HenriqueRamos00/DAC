package com.ufpr.bantads.cliente.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejeitarClienteRequest(
    @NotBlank(message = "Motivo é obrigatório")
    String motivo
) {
}
