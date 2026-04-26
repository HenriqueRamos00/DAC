package com.ufpr.bantads.cliente.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.cliente.application.dto.request.AlterarPerfilRequest;
import com.ufpr.bantads.cliente.application.dto.response.ClienteResponse;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoEncontradoException;
import com.ufpr.bantads.cliente.domain.model.Cliente;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlterarPerfilUseCase {

    private final ClienteRepository clienteRepository;

    public ClienteResponse execute(String cpf, AlterarPerfilRequest request) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new ClienteNaoEncontradoException(cpf));

        if (cliente.getStatus() == StatusCliente.REJEITADO) {
            throw new ClienteNaoEncontradoException(cpf);
        }

        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        cliente.setSalario(request.salario());

        var endereco = cliente.getEndereco();
        endereco.setCep(request.cep());
        endereco.setLogradouro(request.logradouro());
        endereco.setNumero(request.numero());
        endereco.setComplemento(request.complemento());
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.estado());

        Cliente atualizado = clienteRepository.save(cliente);

        return ClienteResponse.fromEntity(atualizado);
    }
}
