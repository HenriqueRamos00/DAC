package com.ufpr.bantads.conta.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufpr.bantads.conta.domain.model.entity.DepositoCommand;

public interface DepositoCommandRepository extends JpaRepository<DepositoCommand, Long> {

    List<DepositoCommand> findByContaId(Long contaId);

}
