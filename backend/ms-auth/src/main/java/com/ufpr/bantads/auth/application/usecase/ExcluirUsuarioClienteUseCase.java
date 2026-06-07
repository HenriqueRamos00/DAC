package com.ufpr.bantads.auth.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.auth.application.dto.command.ExcluirUsuarioClienteCommand;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteExcluidoEvent;
import com.ufpr.bantads.auth.domain.model.entity.Usuario;
import com.ufpr.bantads.auth.domain.model.enums.TipoUsuario;
import com.ufpr.bantads.auth.domain.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcluirUsuarioClienteUseCase {

    private final UsuarioRepository usuarioRepository;

    public UsuarioClienteExcluidoEvent execute(ExcluirUsuarioClienteCommand command) {
        validar(command);

        Usuario usuario = usuarioRepository.findByCpf(command.cpf()).orElse(null);

        if (usuario == null) {
            return new UsuarioClienteExcluidoEvent(command.sagaId(), command.cpf(), command.email());
        }

        validarMesmoUsuario(command, usuario);
        validarTipoCliente(usuario);

        usuarioRepository.delete(usuario);

        return new UsuarioClienteExcluidoEvent(command.sagaId(), usuario.getCpf(), usuario.getEmail());
    }

    private void validar(ExcluirUsuarioClienteCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Dados da exclusão de usuário são obrigatórios");
        }

        if (command.cpf() == null || command.cpf().isBlank()) {
            throw new IllegalArgumentException("CPF do usuário é obrigatório");
        }
    }

    private void validarMesmoUsuario(ExcluirUsuarioClienteCommand command, Usuario usuario) {
        if (command.email() == null || command.email().isBlank()) {
            return;
        }

        if (!command.email().equals(usuario.getEmail())) {
            throw new IllegalStateException("E-mail informado não pertence ao CPF do usuário");
        }
    }

    private void validarTipoCliente(Usuario usuario) {
        if (usuario.getTipoUsuario() != TipoUsuario.CLIENTE) {
            throw new IllegalStateException("Usuário não é cliente");
        }
    }
}
