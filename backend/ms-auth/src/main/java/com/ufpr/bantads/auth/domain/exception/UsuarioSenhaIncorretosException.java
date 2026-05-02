package com.ufpr.bantads.auth.domain.exception;

public class UsuarioSenhaIncorretosException extends RuntimeException {

    public UsuarioSenhaIncorretosException() {
        super("Usuário ou senha incorretos");
    }

}
