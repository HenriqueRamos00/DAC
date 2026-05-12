package com.ufpr.bantads.ms_gerente.domain.exception;

public class GerenteNaoEncontradoException extends RuntimeException {

    public GerenteNaoEncontradoException(String cpf) {
        super("Gerente não encontrado para o CPF " + cpf);
    }
}
