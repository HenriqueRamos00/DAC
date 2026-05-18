package com.ufpr.bantads.ms_gerente.application.dto.event;

import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;

public record GerenteInseridoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String tipo
) {

    public static GerenteInseridoEvent fromResponse(String sagaId, GerenteResponse response) {
        return new GerenteInseridoEvent(
            sagaId,
            response.cpf(),
            response.nome(),
            response.email(),
            response.tipo()
        );
    }
}