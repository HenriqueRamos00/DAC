package com.ufpr.bantads.cliente.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.cliente.application.dto.response.ClienteResponse;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListAllClientesUseCase {

    private final ClienteRepository clienteRepository;

    public List<ClienteResponse> execute() {
        return clienteRepository.findByStatusOrderByNomeAsc(StatusCliente.APROVADO)
            .stream()
            .map(ClienteResponse::fromEntity)
            .toList();
    }
}
