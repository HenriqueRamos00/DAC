package com.ufpr.bantads.ms_gerente.application.usecase;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;
import com.ufpr.bantads.ms_gerente.domain.exception.GerenteNaoEncontradoException;
import com.ufpr.bantads.ms_gerente.domain.repository.GerenteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetGerenteByCpfUseCase {

    private final GerenteRepository gerenteRepository;

    public GerenteResponse execute(String cpf) {
        return gerenteRepository.findByCpf(cpf)
            .map(GerenteResponse::fromEntity)
            .orElseThrow(() -> new GerenteNaoEncontradoException(cpf));
    }

}
