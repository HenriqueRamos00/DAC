package com.ufpr.bantads.cliente.application.usecase;

import com.ufpr.bantads.cliente.application.dto.request.CriarClientePendenteRequest;
import com.ufpr.bantads.cliente.domain.exception.EmailJaCadastradoException;
import com.ufpr.bantads.cliente.domain.exception.TelefoneJaCadastradoException;
import com.ufpr.bantads.cliente.domain.exception.CpfJaCadastradoException;
import com.ufpr.bantads.cliente.domain.model.Cliente;
import com.ufpr.bantads.cliente.domain.model.Endereco;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriarClientePendenteUseCase {

    private final ClienteRepository repository;

    public Cliente execute(CriarClientePendenteRequest request) {
        if (repository.existsByCpf(request.cpf())) {
            throw new CpfJaCadastradoException(request.cpf());
        }
        if (repository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }
        if (repository.existsByTelefone(request.telefone())) {
            throw new TelefoneJaCadastradoException(request.telefone());
        }

        Cliente cliente = new Cliente();
        cliente.setCpf(request.cpf());
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        cliente.setSalario(request.salario());
        cliente.setEndereco(buildEndereco(request));
        cliente.setStatus(StatusCliente.PENDENTE);

        return repository.save(cliente);
    }

    private Endereco buildEndereco(CriarClientePendenteRequest request) {
        Endereco endereco = new Endereco();
        endereco.setCep(request.cep());
        endereco.setLogradouro(request.logradouro());
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.estado());
        endereco.setComplemento(request.complemento());
        endereco.setNumero(request.numero());
        return endereco;
    }
}
