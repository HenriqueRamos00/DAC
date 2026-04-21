package com.ufpr.bantads.cliente.application.dto.response;

import com.ufpr.bantads.cliente.domain.model.Cliente;

public record ClienteResponse(
    String cpf,
    String nome,
    String email,
    String telefone,
    double salario,
    String endereco,
    String CEP,
    String cidade,
    String estado
) {
    public static ClienteResponse fromEntity(Cliente cliente) {
        var end = cliente.getEndereco();
        String enderecoStr = end != null
            ? end.getLogradouro() + ", " + end.getNumero()
            : "";

        return new ClienteResponse(
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getSalario().doubleValue(),
            enderecoStr,
            end != null ? end.getCep() : "",
            end != null ? end.getCidade() : "",
            end != null ? end.getEstado() : ""
        );
    }
}
