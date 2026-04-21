package com.ufpr.bantads.cliente.domain.exception;

public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException(String cpf) {
        super("Cliente nao encontrado para o CPF " + cpf);
    }
}
