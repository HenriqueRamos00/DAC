package com.ufpr.bantads.cliente.application.usecase;

import java.util.regex.*;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.cliente.application.dto.command.AlterarPerfilClienteCommand;
import com.ufpr.bantads.cliente.application.dto.event.ClientePerfilAlteradoEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClientePerfilAlteradoEvent.DadosPerfil;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoEncontradoException;
import com.ufpr.bantads.cliente.domain.model.Cliente;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlterarPerfilUseCase {

    private final ClienteRepository clienteRepository;
    private final String regex = ",\\s*(\\d+)$";
    private final Pattern pattern = Pattern.compile(regex);

    public ClientePerfilAlteradoEvent execute(
        String sagaId,
        String cpf, 
        AlterarPerfilClienteCommand request) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new ClienteNaoEncontradoException(cpf));

        if (cliente.getStatus() == StatusCliente.REJEITADO) {
            throw new ClienteNaoEncontradoException(cpf);
        }

        DadosPerfil dadosAnteriores = DadosPerfil.fromEntity(cliente);

        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        cliente.setSalario(request.salario());

        var endereco = cliente.getEndereco();
        endereco.setCep(request.cep());
        Matcher matcher = pattern.matcher(request.logradouro());
        if (matcher.find()) {
            String numero = matcher.group(1);
            String logradouro = request.logradouro().replaceFirst(regex, "");
            endereco.setLogradouro(logradouro);
            endereco.setNumero(numero);
        } else {
            endereco.setLogradouro(request.logradouro());
        }
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.estado());

        Cliente atualizado = clienteRepository.save(cliente);

        return ClientePerfilAlteradoEvent.fromEntity(sagaId, atualizado, dadosAnteriores);
    }
}
