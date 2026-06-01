package com.ufpr.bantads.ms_gerente.application.dto.event;

import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;

public record GerenteAtivoDetalhado(
    String cpf,
    String nome,
    String email,
    String tipo
) {
    public static GerenteAtivoDetalhado fromResponse(GerenteResponse response) {
        return new GerenteAtivoDetalhado(
            response.cpf(),
            response.nome(),
            response.email(),
            response.tipo()
        );
    }
}
