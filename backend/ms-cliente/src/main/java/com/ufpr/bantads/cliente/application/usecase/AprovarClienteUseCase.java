package com.ufpr.bantads.cliente.application.usecase;

import com.ufpr.bantads.cliente.domain.model.Cliente;
import com.ufpr.bantads.cliente.application.dto.response.ClienteResponse;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoEncontradoException;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoPendenteException;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AprovarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public ClienteResponse execute(String cpf) {
        Cliente cliente = executeAndReturnEntity(cpf);
        return ClienteResponse.fromEntity(cliente);
    }

    private Cliente executeAndReturnEntity(String cpf) {
        var cliente = clienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new ClienteNaoEncontradoException(cpf));

        if (cliente.getStatus() != StatusCliente.PENDENTE) {
            throw new ClienteNaoPendenteException(cpf);
        }

        cliente.setStatus(StatusCliente.APROVADO);
        cliente.setDataAprovacao(LocalDateTime.now());
        cliente.setMotivoRejeicao(null);

        return clienteRepository.save(cliente);
    }
}
