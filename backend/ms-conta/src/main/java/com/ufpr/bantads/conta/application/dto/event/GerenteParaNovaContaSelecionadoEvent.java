package com.ufpr.bantads.conta.application.dto.event;

import com.ufpr.bantads.conta.application.dto.shared.GerenteCandidato;

public record GerenteParaNovaContaSelecionadoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email
) {
    public static GerenteParaNovaContaSelecionadoEvent from(String sagaId, GerenteCandidato gerente) {
        return new GerenteParaNovaContaSelecionadoEvent(
            sagaId,
            gerente.cpf(),
            gerente.nome(),
            gerente.email()
        );
    }
}
