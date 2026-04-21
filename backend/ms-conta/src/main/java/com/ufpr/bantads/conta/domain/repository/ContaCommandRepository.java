package com.ufpr.bantads.conta.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;

public interface ContaCommandRepository extends JpaRepository<ContaCommand, Long> {

    Optional<ContaCommand> findByNumeroConta(String numeroConta);

    Optional<ContaCommand> findByClienteCpf(String clienteCpf);

}
