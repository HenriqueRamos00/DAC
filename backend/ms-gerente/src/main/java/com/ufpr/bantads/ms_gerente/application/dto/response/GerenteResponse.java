package com.ufpr.bantads.ms_gerente.application.dto.response;

import com.ufpr.bantads.ms_gerente.domain.model.entity.Gerente;

public record GerenteResponse(
    String nome,
    String email,
    String cpf,
    String telefone
) {
    public static GerenteResponse fromEntity(Gerente gerente) {
        return new GerenteResponse(
            gerente.getNome(),
            gerente.getEmail(),
            gerente.getCpf(),
            gerente.getTelefone()
        );
    }
}
