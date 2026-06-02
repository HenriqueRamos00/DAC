package com.ufpr.bantads.cliente.application.dto.request;

import com.ufpr.bantads.cliente.domain.model.Cliente;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Locale;

public record CriarClientePendenteRequest(
    Long id,

    @NotBlank(message = "CPF é obrigatório")
    String cpf,

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

    @JsonAlias("CEP")
    String cep,

    String endereco,

    String logradouro,

    @NotBlank(message = "Cidade é obrigatória")
    String cidade,

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres, por exemplo: PR")
    String estado,

    String complemento,

    String numero
) {
    public CriarClientePendenteRequest {
        estado = estado == null ? null : estado.trim().toUpperCase(Locale.ROOT);
    }

    @AssertTrue(message = "Endereço é obrigatório")
    public boolean isEnderecoInformado() {
        return hasText(endereco) || hasText(logradouro);
    }

    public String enderecoNormalizado() {
        return hasText(endereco) ? endereco : logradouro;
    }

    public String cepNormalizado() {
        return cep == null ? "" : cep;
    }

    public String numeroNormalizado() {
        return numero == null ? "" : numero;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public static CriarClientePendenteRequest fromEntity(Cliente cliente) {
        return new CriarClientePendenteRequest(
            cliente.getId(),
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getSalario(),
            cliente.getEndereco().getCep(),
            cliente.getEndereco().toString(),
            cliente.getEndereco().getLogradouro(),
            cliente.getEndereco().getCidade(),
            cliente.getEndereco().getEstado(),
            cliente.getEndereco().getComplemento(),
            cliente.getEndereco().getNumero()
        );
    }

}
