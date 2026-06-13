package com.ufpr.bantads.conta.domain.exception;

public class ContaNaoEncontradaException extends RuntimeException {

    public ContaNaoEncontradaException() {
        super("Conta não encontrada");
    }

    public ContaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
