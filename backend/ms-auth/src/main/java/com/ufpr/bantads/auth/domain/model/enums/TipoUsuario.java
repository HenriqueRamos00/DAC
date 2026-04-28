package com.ufpr.bantads.auth.domain.model.enums;

public enum TipoUsuario {
    CLIENTE,
    GERENTE,
    ADMINISTRADOR;

    public static TipoUsuario fromString(String value) {
        return TipoUsuario.valueOf(value.toUpperCase());
    }
}
