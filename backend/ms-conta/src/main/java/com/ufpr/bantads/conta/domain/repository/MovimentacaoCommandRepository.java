package com.ufpr.bantads.conta.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufpr.bantads.conta.domain.model.entity.MovimentacaoCommand;

public interface MovimentacaoCommandRepository extends JpaRepository<MovimentacaoCommand, Long> {

    List<MovimentacaoCommand> findByConta_id(Long conta_id);

}
