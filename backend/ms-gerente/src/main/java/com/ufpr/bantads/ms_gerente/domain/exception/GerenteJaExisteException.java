package com.ufpr.bantads.ms_gerente.domain.exception;

public class GerenteJaExisteException extends RuntimeException {

    public GerenteJaExisteException() {
        super("Gerente já existe");
    }
}
