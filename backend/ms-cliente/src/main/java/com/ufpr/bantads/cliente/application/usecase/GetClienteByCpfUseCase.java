package com.ufpr.bantads.cliente.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.cliente.application.dto.response.ClienteResponse;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoEncontradoException;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetClienteByCpfUseCase {

    private final ClienteRepository clienteRepository;

    public ClienteResponse execute(String cpf) {
        var cliente = clienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new ClienteNaoEncontradoException(cpf));

        if (cliente.getStatus() == StatusCliente.REJEITADO) {
            throw new ClienteNaoEncontradoException(cpf);
        }

        return ClienteResponse.fromEntity(cliente);
    }
}
