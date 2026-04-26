package com.ufpr.bantads.cliente.domain.exception;

public class ClienteNaoPendenteException extends RuntimeException {

    public ClienteNaoPendenteException(String cpf) {
        super("Cliente com CPF " + cpf + " já está aprovado");
    }
}
