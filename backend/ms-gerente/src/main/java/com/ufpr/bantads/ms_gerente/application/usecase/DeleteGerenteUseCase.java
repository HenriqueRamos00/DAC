package com.ufpr.bantads.ms_gerente.application.usecase;

import com.ufpr.bantads.ms_gerente.domain.exception.GerenteNaoEncontradoException;
import com.ufpr.bantads.ms_gerente.domain.model.entity.Gerente;
import com.ufpr.bantads.ms_gerente.domain.repository.GerenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteGerenteUseCase {

    private final GerenteRepository gerenteRepository;

    @Transactional
    public void execute(String cpf) {
        Gerente gerente = gerenteRepository.findByCpf(cpf)
            .orElseThrow(() -> new GerenteNaoEncontradoException(cpf));

        gerenteRepository.delete(gerente);
    }
}
