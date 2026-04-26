package com.ufpr.bantads.cliente.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Locale;

public record AlterarPerfilRequest(

    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Telefone é obrigatório")
    String telefone,

    @NotNull(message = "Salário é obrigatório")
    @DecimalMin(value = "0.01", message = "Salário deve ser maior que zero")
    BigDecimal salario,

    @NotBlank(message = "CEP é obrigatório")
    String cep,

    @NotBlank(message = "Logradouro é obrigatório")
    String logradouro,

    @NotBlank(message = "Cidade é obrigatória")
    String cidade,

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres, por exemplo: PR")
    String estado,

    String complemento,

    @NotBlank(message = "Número é obrigatório")
    String numero
) {
    public AlterarPerfilRequest {
        estado = estado == null ? null : estado.trim().toUpperCase(Locale.ROOT);
    }
}
