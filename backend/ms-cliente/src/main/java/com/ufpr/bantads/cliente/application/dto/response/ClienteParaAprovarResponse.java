package com.ufpr.bantads.cliente.application.dto.response;

import com.ufpr.bantads.cliente.domain.model.Cliente;

public record ClienteParaAprovarResponse(
    String cpf,
    String nome,
    String email,
    double salario,
    String endereco,
    String cidade,
    String estado,
    String status
) {
    public static ClienteParaAprovarResponse fromEntity(Cliente cliente) {
        var end = cliente.getEndereco();
        String enderecoStr = end != null
            ? end.getLogradouro() + ", " + end.getNumero()
            : "";

        return new ClienteParaAprovarResponse(
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getSalario().doubleValue(),
            enderecoStr,
            end != null ? end.getCidade() : "",
            end != null ? end.getEstado() : "",
            cliente.getStatus().toString()
        );
    }
}
