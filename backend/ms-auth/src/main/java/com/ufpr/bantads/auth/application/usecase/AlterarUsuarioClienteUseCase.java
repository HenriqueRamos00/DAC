package com.ufpr.bantads.auth.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.auth.application.dto.command.AlterarUsuarioClienteCommand;
import com.ufpr.bantads.auth.application.dto.command.ReverterAlteracaoUsuarioClienteCommand;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteAlteradoEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteRevertidoEvent;
import com.ufpr.bantads.auth.domain.model.entity.Usuario;
import com.ufpr.bantads.auth.domain.model.enums.TipoUsuario;
import com.ufpr.bantads.auth.domain.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlterarUsuarioClienteUseCase {

    private final UsuarioRepository usuarioRepository;

    public UsuarioClienteAlteradoEvent alterar(AlterarUsuarioClienteCommand command) {
        validar(command.sagaId(), command.cpf(), command.email());

        Usuario usuario = buscarCliente(command.cpf());
        validarEmailDisponivel(command.email(), command.cpf());

        usuario.setNome(command.nome());
        usuario.setEmail(command.email());
        usuarioRepository.save(usuario);

        return new UsuarioClienteAlteradoEvent(
            command.sagaId(),
            usuario.getCpf(),
            usuario.getNome(),
            usuario.getEmail(),
            command.nomeAnterior(),
            command.emailAnterior()
        );
    }

    public UsuarioClienteRevertidoEvent reverter(ReverterAlteracaoUsuarioClienteCommand command) {
        validar(command.sagaId(), command.cpf(), command.email());

        Usuario usuario = buscarCliente(command.cpf());
        validarEmailDisponivel(command.email(), command.cpf());

        usuario.setNome(command.nome());
        usuario.setEmail(command.email());
        usuarioRepository.save(usuario);

        return new UsuarioClienteRevertidoEvent(
            command.sagaId(),
            usuario.getCpf(),
            usuario.getNome(),
            usuario.getEmail()
        );
    }

    private Usuario buscarCliente(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
            .orElseThrow(() -> new IllegalStateException("Usuário cliente não encontrado"));

        if (usuario.getTipoUsuario() != TipoUsuario.CLIENTE) {
            throw new IllegalStateException("Usuário não é cliente");
        }

        return usuario;
    }

    private void validar(String sagaId, String cpf, String email) {
        if (sagaId == null || sagaId.isBlank()) {
            throw new IllegalArgumentException("SagaId é obrigatório");
        }

        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF do usuário é obrigatório");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail do usuário é obrigatório");
        }
    }

    private void validarEmailDisponivel(String email, String cpf) {
        usuarioRepository.findByEmail(email)
            .filter(usuario -> !cpf.equals(usuario.getCpf()))
            .ifPresent(usuario -> {
                throw new IllegalStateException("E-mail já está em uso");
            });
    }
}
