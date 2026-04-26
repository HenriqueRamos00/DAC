package com.ufpr.bantads.cliente.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.cliente.application.dto.response.ClienteParaAprovarResponse;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListClientesParaAprovarUseCase {

    private final ClienteRepository clienteRepository;

    public List<ClienteParaAprovarResponse> execute() {
        return clienteRepository.findByStatusOrderByCreatedAtAsc(StatusCliente.PENDENTE)
            .stream()
            .map(ClienteParaAprovarResponse::fromEntity)
            .toList();
    }
}
