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
        String cpf = onlyDigits(request.cpf());

        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF inválido");
        }

        if (gerenteRepository.existsByCpf(cpf) || gerenteRepository.existsByEmail(request.email())) {
            throw new GerenteJaExisteException();
        }

        Gerente gerente = new Gerente();
        gerente.setCpf(cpf);
        gerente.setNome(request.nome());
        gerente.setEmail(request.email());
        gerente.setTelefone(request.telefone());

        return GerenteResponse.fromEntity(gerenteRepository.save(gerente));
    }

    private String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }
}
