package com.ufpr.bantads.cliente.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufpr.bantads.cliente.domain.model.Cliente;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    List<Cliente> findByStatus(StatusCliente status);

    boolean existsByCpf(String cpf);
}
