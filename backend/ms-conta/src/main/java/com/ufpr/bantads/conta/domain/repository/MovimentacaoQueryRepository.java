package com.ufpr.bantads.conta.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufpr.bantads.conta.domain.model.entity.MovimentacaoQuery;

public interface MovimentacaoQueryRepository extends JpaRepository<MovimentacaoQuery, Long> {

    List<MovimentacaoQuery> findByTipo(String tipo);

}
