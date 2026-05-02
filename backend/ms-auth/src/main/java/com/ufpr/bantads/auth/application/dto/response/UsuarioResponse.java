package com.ufpr.bantads.auth.application.dto.response;

import com.ufpr.bantads.auth.domain.model.entity.Usuario;

public record UsuarioResponse(
    String nome,
    String cpf,
    String email,
    String tipoUsuario
) {
    public static UsuarioResponse fromEntity(Usuario usuario) 
    {
        return new UsuarioResponse( 
            usuario.getNome(),
            usuario.getCpf(), 
            usuario.getEmail(), 
            usuario.getTipoUsuario().toString());
    }
}
