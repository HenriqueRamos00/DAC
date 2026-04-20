package com.ufpr.bantads.conta.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;

public interface ContaQueryRepository extends JpaRepository<ContaQuery, Long> {

    Optional<ContaQuery> findByNumero_conta(String numero_conta);

    List<ContaQuery> findByCliente_cpf(String cliente_cpf);

    List<ContaQuery> findByGerente_cpf(String gerente_cpf);

}
