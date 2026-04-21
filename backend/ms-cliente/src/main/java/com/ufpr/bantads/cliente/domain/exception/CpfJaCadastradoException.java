package com.ufpr.bantads.cliente.domain.exception;

public class CpfJaCadastradoException extends RuntimeException {

    public CpfJaCadastradoException(String cpf) {
        super("Ja existe cliente cadastrado com o CPF " + cpf);
    }
}
