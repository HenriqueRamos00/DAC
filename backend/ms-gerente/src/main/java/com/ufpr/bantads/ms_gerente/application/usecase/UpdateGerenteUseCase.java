package com.ufpr.bantads.ms_gerente.application.usecase;

import com.ufpr.bantads.ms_gerente.application.dto.request.GerenteRequest;
import com.ufpr.bantads.ms_gerente.application.dto.response.GerenteResponse;
import com.ufpr.bantads.ms_gerente.domain.exception.GerenteJaExisteException;
import com.ufpr.bantads.ms_gerente.domain.exception.GerenteNaoEncontradoException;
import com.ufpr.bantads.ms_gerente.domain.model.entity.Gerente;
import com.ufpr.bantads.ms_gerente.domain.repository.GerenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateGerenteUseCase {

    private final GerenteRepository gerenteRepository;

    @Transactional
    public GerenteResponse execute(String cpf, GerenteRequest request) {
        Gerente gerente = gerenteRepository.findByCpf(cpf)
            .orElseThrow(() -> new GerenteNaoEncontradoException(cpf));

        if (gerenteRepository.existsByEmailAndCpfNot(request.email(), cpf)) {
            throw new GerenteJaExisteException();
        }

        gerente.setNome(request.nome());
        gerente.setEmail(request.email());
        if (request.telefone() != null) {
            gerente.setTelefone(request.telefone());
        }

        return GerenteResponse.fromEntity(gerenteRepository.save(gerente));
    }
}
