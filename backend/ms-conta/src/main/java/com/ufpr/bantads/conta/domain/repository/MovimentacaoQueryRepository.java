package com.ufpr.bantads.conta.domain.repository;

import com.ufpr.bantads.conta.domain.model.enums.TipoMovimentacao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufpr.bantads.conta.domain.model.entity.MovimentacaoQuery;

public interface MovimentacaoQueryRepository extends JpaRepository<MovimentacaoQuery, Long> {

    boolean existsByEventId(String eventId);

    List<MovimentacaoQuery> findByTipo(TipoMovimentacao tipo);

    List<MovimentacaoQuery> findByContaOrigemNumeroOrContaDestinoNumero(String contaOrigemNumero, String contaDestinoNumero);

}
