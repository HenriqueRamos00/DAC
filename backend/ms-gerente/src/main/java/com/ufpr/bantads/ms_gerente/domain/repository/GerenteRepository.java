package com.ufpr.bantads.ms_gerente.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufpr.bantads.ms_gerente.domain.model.entity.Gerente;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    public Optional<Gerente> findByCpf(String cpf);
}
