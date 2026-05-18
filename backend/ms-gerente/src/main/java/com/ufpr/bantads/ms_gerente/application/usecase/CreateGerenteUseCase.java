package com.ufpr.bantads.ms_gerente.application.usecase;

import com.ufpr.bantads.ms_gerente.application.dto.request.GerenteRequest;
import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;
import com.ufpr.bantads.ms_gerente.domain.exception.GerenteJaExisteException;
import com.ufpr.bantads.ms_gerente.domain.model.entity.Gerente;
import com.ufpr.bantads.ms_gerente.domain.repository.GerenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateGerenteUseCase {

    private final GerenteRepository gerenteRepository;

    @Transactional
    public GerenteResponse execute(GerenteRequest request) {
        if (gerenteRepository.existsByCpf(request.cpf()) || gerenteRepository.existsByEmail(request.email())) {
            throw new GerenteJaExisteException();
        }

        Gerente gerente = new Gerente();
        gerente.setCpf(request.cpf());
        gerente.setNome(request.nome());
        gerente.setEmail(request.email());
        gerente.setTelefone(request.telefone());

        return GerenteResponse.fromEntity(gerenteRepository.save(gerente));
    }
}
