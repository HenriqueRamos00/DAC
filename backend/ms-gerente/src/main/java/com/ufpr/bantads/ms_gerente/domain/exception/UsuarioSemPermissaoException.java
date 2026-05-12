package com.ufpr.bantads.ms_gerente.domain.exception;

public class UsuarioSemPermissaoException extends RuntimeException {

    public UsuarioSemPermissaoException() {
        super("O usuário não tem permissão para efetuar esta operação");
    }
}
