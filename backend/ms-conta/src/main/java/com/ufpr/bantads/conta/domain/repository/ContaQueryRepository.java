package com.ufpr.bantads.conta.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;

public interface ContaQueryRepository extends JpaRepository<ContaQuery, Long> {

    Optional<ContaQuery> findByNumeroConta(String numeroConta);

    Optional<ContaQuery> findByClienteCpf(String clienteCpf);

    List<ContaQuery> findByGerenteCpf(String gerenteCpf);

}
