package com.ufpr.bantads.conta.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufpr.bantads.conta.domain.model.entity.SaqueCommand;

public interface SaqueCommandRepository extends JpaRepository<SaqueCommand, Long> {

    List<SaqueCommand> findByContaId(Long contaId);

}
