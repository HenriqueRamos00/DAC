package com.ufpr.bantads.auth.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.auth.domain.exception.UsuarioSenhaIncorretosException;
import com.ufpr.bantads.auth.domain.model.entity.Usuario;
import com.ufpr.bantads.auth.domain.repository.UsuarioRepository;
import com.ufpr.bantads.auth.infrastructure.security.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public Usuario execute(String login, String senha) {
        var usuarioOpt = usuarioRepository.findByEmail(login);
        if (usuarioOpt.isEmpty()) {
            usuarioOpt = usuarioRepository.findByCpf(login);
            if (usuarioOpt.isEmpty()) {
                throw new UsuarioSenhaIncorretosException();
            }
        }
        var usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new UsuarioSenhaIncorretosException();
        }
        return usuario;
    }
}
