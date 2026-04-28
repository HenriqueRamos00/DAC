package com.ufpr.bantads.auth.domain.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ufpr.bantads.auth.domain.model.entity.Usuario;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
