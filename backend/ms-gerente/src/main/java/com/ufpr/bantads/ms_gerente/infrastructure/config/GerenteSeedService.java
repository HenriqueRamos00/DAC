package com.ufpr.bantads.ms_gerente.infrastructure.config;

import com.ufpr.bantads.ms_gerente.domain.model.entity.Gerente;
import com.ufpr.bantads.ms_gerente.domain.repository.GerenteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GerenteSeedService {

    private final GerenteRepository gerenteRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedOnStartup() {
        applySeedIfEmpty();
    }

    @Transactional
    public void reboot() {
        gerenteRepository.deleteAllInBatch();
        applySeed();
    }

    private void applySeedIfEmpty() {
        if (gerenteRepository.count() == 0) {
            applySeed();
        }
    }

    private void applySeed() {
        gerenteRepository.saveAll(List.of(
            gerenteSeed("Geniéve", "ger1@bantads.com.br", "98574307084", "41999990001"),
            gerenteSeed("Godophredo", "ger2@bantads.com.br", "64065268052", "41999990002"),
            gerenteSeed("Gyândula", "ger3@bantads.com.br", "23862179060", "41999990003")
        ));
    }

    private Gerente gerenteSeed(String nome, String email, String cpf, String telefone) {
        Gerente gerente = new Gerente();
        gerente.setNome(nome);
        gerente.setEmail(email);
        gerente.setCpf(cpf);
        gerente.setTelefone(telefone);
        return gerente;
    }
}
