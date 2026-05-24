package com.ufpr.bantads.conta.domain.exception;

public class NumeroContaIndisponivelException extends RuntimeException {

    public NumeroContaIndisponivelException() {
        super("Não foi possível gerar um número de conta disponível");
    }
}
