package com.ufpr.bantads.cliente.domain.exception;

public class ComandoCompensacaoInvalidoException extends RuntimeException {

    public ComandoCompensacaoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
