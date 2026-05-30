package br.ufpr.bantads.saga.shared.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufpr.bantads.saga.shared.entity.SagaStep;
import br.ufpr.bantads.saga.shared.enums.SagaStepStatus;

public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

    Optional<SagaStep> findBySagaSagaIdAndStepNameAndStatus(
        String sagaId,
        String stepName,
        SagaStepStatus status
    );

    Optional<SagaStep> findFirstBySagaSagaIdAndStepNameAndStatusOrderByIdDesc(
        String sagaId,
        String stepName,
        SagaStepStatus status
    );

}
