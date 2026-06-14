package com.ufpr.bantads.cliente.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.cliente.application.dto.command.ReverterAlteracaoPerfilClienteCommand;
import com.ufpr.bantads.cliente.application.dto.event.ClientePerfilRevertidoEvent;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoEncontradoException;
import com.ufpr.bantads.cliente.domain.exception.ComandoCompensacaoInvalidoException;
import com.ufpr.bantads.cliente.domain.model.Cliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReverterAlteracaoPerfilUseCase {

    private final ClienteRepository clienteRepository;

    public ClientePerfilRevertidoEvent execute(ReverterAlteracaoPerfilClienteCommand command) {
        validar(command);

        Cliente cliente = clienteRepository.findByCpf(command.cpf())
            .orElseThrow(() -> new ClienteNaoEncontradoException(command.cpf()));

        cliente.setNome(command.nome());
        cliente.setEmail(command.email());
        cliente.setTelefone(command.telefone());
        cliente.setSalario(command.salario());

        var endereco = cliente.getEndereco();
        endereco.setCep(command.cep());
        endereco.setLogradouro(command.logradouro());
        endereco.setCidade(command.cidade());
        endereco.setEstado(command.estado());
        endereco.setComplemento(command.complemento());
        endereco.setNumero(command.numero());

        Cliente atualizado = clienteRepository.save(cliente);

        return ClientePerfilRevertidoEvent.fromEntity(command.sagaId(), atualizado);
    }

    private void validar(ReverterAlteracaoPerfilClienteCommand command) {
        if (command == null || command.cpf() == null || command.cpf().isBlank()) {
            throw new ComandoCompensacaoInvalidoException("Dados de reversão do perfil são obrigatórios");
        }

        validarCampo(command.nome(), "Nome anterior do cliente é obrigatório");
        validarCampo(command.email(), "E-mail anterior do cliente é obrigatório");
        validarCampo(command.cep(), "CEP anterior do cliente é obrigatório");
        validarCampo(command.logradouro(), "Logradouro anterior do cliente é obrigatório");
        validarCampo(command.cidade(), "Cidade anterior do cliente é obrigatória");
        validarCampo(command.estado(), "Estado anterior do cliente é obrigatório");

        if (command.salario() == null) {
            throw new ComandoCompensacaoInvalidoException("Salário anterior do cliente é obrigatório");
        }
    }

    private void validarCampo(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ComandoCompensacaoInvalidoException(mensagem);
        }
    }
}
