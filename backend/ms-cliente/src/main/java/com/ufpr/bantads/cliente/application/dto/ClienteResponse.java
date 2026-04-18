package com.ufpr.bantads.cliente.application.dto;

import java.math.BigDecimal;

import com.ufpr.bantads.cliente.domain.model.Cliente;

public record ClienteResponse(
    Long id,
    String cpf,
    String nome,
    String email,
    String telefone,
    BigDecimal salario,
    String cep,
    // String uf,
    String logradouro,
    String cidade,
    // String bairro,
    String complemento,
    String numero
) {
    public static ClienteResponse fromEntity(Cliente cliente) {
        return new ClienteResponse(
            cliente.getId(),
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getSalario(),
            cliente.getEndereco().getCep(),
            // cliente.getEndereco().getUf(),
            cliente.getEndereco().getLogradouro(),
            cliente.getEndereco().getCidade(),
            // cliente.getEndereco().getBairro(),
            cliente.getEndereco().getComplemento(),
            cliente.getEndereco().getNumero()
        );
    }   

}
