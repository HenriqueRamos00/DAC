package com.ufpr.bantads.ms_gerente.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;
import com.ufpr.bantads.ms_gerente.domain.repository.GerenteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListAllGerentesUseCase {

    private final GerenteRepository gerenteRepository;

    public List<GerenteResponse> execute() {
        return gerenteRepository.findAll()
            .stream()
            .map(GerenteResponse::fromEntity)
            .toList();
    }
}
