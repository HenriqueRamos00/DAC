package com.ufpr.bantads.cliente.domain.exception;

public class TelefoneJaCadastradoException extends RuntimeException {

    public TelefoneJaCadastradoException(String telefone) {
        super("Ja existe cliente cadastrado com o telefone " + telefone);
    }
}
