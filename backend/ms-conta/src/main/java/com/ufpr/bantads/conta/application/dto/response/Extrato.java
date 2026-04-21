package com.ufpr.bantads.conta.application.dto.response;

import com.ufpr.bantads.conta.domain.model.entity.MovimentacaoQuery;

public record Extrato(
    String data,
    String tipo,
    String origem,
    String destino,
    Double valor
) {
    public static Extrato fromEntity(MovimentacaoQuery movimentacaoQuery) {
        return new Extrato(
            movimentacaoQuery.getDataHora().toString(),
            movimentacaoQuery.getTipo().name(),
            movimentacaoQuery.getContaOrigemNumero(),
            movimentacaoQuery.getContaDestinoNumero(),
            movimentacaoQuery.getValor().doubleValue()
        );
    }
}
