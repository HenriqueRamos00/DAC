package br.ufpr.bantads.saga.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufpr.bantads.saga.shared.entity.SagaInstance;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, String> {

}
