package com.ufpr.bantads.auth.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.auth.application.dto.command.ExcluirUsuarioGerenteCompensacaoCommand;
import com.ufpr.bantads.auth.application.dto.event.UsuarioGerenteExcluidoCompensacaoEvent;
import com.ufpr.bantads.auth.domain.model.entity.Usuario;
import com.ufpr.bantads.auth.domain.model.enums.TipoUsuario;
import com.ufpr.bantads.auth.domain.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcluirUsuarioGerenteUseCase {

    private final UsuarioRepository usuarioRepository;

    public UsuarioGerenteExcluidoCompensacaoEvent execute(ExcluirUsuarioGerenteCompensacaoCommand command) {
        validar(command);

        Usuario usuario = usuarioRepository.findByCpf(command.cpf()).orElse(null);

        if (usuario == null) {
            return new UsuarioGerenteExcluidoCompensacaoEvent(command.sagaId(), command.cpf(), command.email());
        }

        validarTipoGerente(usuario);

        usuarioRepository.delete(usuario);

        return new UsuarioGerenteExcluidoCompensacaoEvent(command.sagaId(), usuario.getCpf(), usuario.getEmail());
    }

    private void validar(ExcluirUsuarioGerenteCompensacaoCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Dados da exclusão de usuário são obrigatórios");
        }

        if (command.cpf() == null || command.cpf().isBlank()) {
            throw new IllegalArgumentException("CPF do usuário é obrigatório");
        }
    }

    private void validarTipoGerente(Usuario usuario) {
        if (usuario.getTipoUsuario() != TipoUsuario.GERENTE) {
            throw new IllegalStateException("Usuário não é gerente");
        }
    }
}