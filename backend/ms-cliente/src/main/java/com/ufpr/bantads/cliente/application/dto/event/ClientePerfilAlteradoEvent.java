package com.ufpr.bantads.cliente.application.dto.event;

import com.ufpr.bantads.cliente.domain.model.Cliente;
import java.math.BigDecimal;

public record ClientePerfilAlteradoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String telefone,
    BigDecimal salario,
    String cep,
    String logradouro,
    String cidade,
    String estado
) {
    public static ClientePerfilAlteradoEvent fromEntity(String sagaId, Cliente cliente) {
        var endereco = cliente.getEndereco();

        return new ClientePerfilAlteradoEvent(
            sagaId,
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getSalario(),
            endereco.getCep(),
            endereco.toString(),
            endereco.getCidade(),
            endereco.getEstado()
        );
    }
}
