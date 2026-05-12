package com.ufpr.bantads.ms_gerente.domain.exception;

public class UsuarioNaoAutenticadoException extends RuntimeException {

    public UsuarioNaoAutenticadoException() {
        super("O usuário não está logado");
    }
}
