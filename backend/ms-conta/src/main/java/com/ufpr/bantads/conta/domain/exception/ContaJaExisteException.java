package com.ufpr.bantads.conta.domain.exception;

public class ContaJaExisteException extends RuntimeException {

    public ContaJaExisteException(String clienteCpf) {
        super("Cliente " + clienteCpf + " já possui conta");
    }
}
