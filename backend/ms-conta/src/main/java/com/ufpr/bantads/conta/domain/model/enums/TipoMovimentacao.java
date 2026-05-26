package com.ufpr.bantads.conta.domain.model.enums;

public enum TipoMovimentacao {
    SAQUE("saque"),
    DEPOSITO("depósito"),
    TRANSFERENCIA("transferência");

    private final String valor;

    TipoMovimentacao(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return valor;
    }
}