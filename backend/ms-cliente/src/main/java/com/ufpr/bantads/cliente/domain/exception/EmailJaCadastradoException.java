package com.ufpr.bantads.cliente.domain.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("Ja existe cliente cadastrado com o email " + email);
    }
}
