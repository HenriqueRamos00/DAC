package com.ufpr.bantads.auth.application.usecase;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.auth.application.dto.request.CriarUsuarioRequest;
import com.ufpr.bantads.auth.application.dto.response.CriarUsuarioResponse;
import com.ufpr.bantads.auth.domain.exception.CpfJaCadastradoException;
import com.ufpr.bantads.auth.domain.exception.EmailJaCadastradoException;
import com.ufpr.bantads.auth.domain.model.entity.Usuario;
import com.ufpr.bantads.auth.domain.repository.UsuarioRepository;
import com.ufpr.bantads.auth.infrastructure.security.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SENHA_LENGTH = 8;

    public CriarUsuarioResponse execute(CriarUsuarioRequest request) {
        if (usuarioRepository.existsByCpf(request.cpf())) {
            throw new CpfJaCadastradoException(request.cpf());
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        String senhaPlana = request.senha() != null && !request.senha().isBlank()
            ? request.senha()
            : gerarSenhaAleatoria();

        String senhaHash = passwordEncoder.encode(senhaPlana);

        Usuario usuario = new Usuario();
        usuario.setCpf(request.cpf());
        usuario.setEmail(request.email());
        usuario.setSenha(senhaHash);
        usuario.setTipoUsuario(request.tipoUsuario());

        usuarioRepository.save(usuario);

        return new CriarUsuarioResponse(
            usuario.getCpf(),
            usuario.getEmail(),
            usuario.getTipoUsuario(),
            senhaPlana
        );
    }

    private String gerarSenhaAleatoria() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(SENHA_LENGTH);
        for (int i = 0; i < SENHA_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
