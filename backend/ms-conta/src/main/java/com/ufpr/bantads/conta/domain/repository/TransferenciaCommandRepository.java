package com.ufpr.bantads.conta.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufpr.bantads.conta.domain.model.entity.TransferenciaCommand;

public interface TransferenciaCommandRepository extends JpaRepository<TransferenciaCommand, Long> {

    List<TransferenciaCommand> findByContaOrigemId(Long contaOrigemId);

    List<TransferenciaCommand> findByContaDestinoId(Long contaDestinoId);

}
