package br.ufpr.bantads.saga.shared.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufpr.bantads.saga.shared.entity.SagaInstance;
import br.ufpr.bantads.saga.shared.entity.SagaStep;
import br.ufpr.bantads.saga.shared.enums.SagaStatus;
import br.ufpr.bantads.saga.shared.enums.SagaStepStatus;
import br.ufpr.bantads.saga.shared.repository.SagaInstanceRepository;
import br.ufpr.bantads.saga.shared.repository.SagaStepRepository;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class SagaPersistenceService {

    private final SagaInstanceRepository sagaRepository;
    private final SagaStepRepository stepRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createSaga(String sagaId, String sagaType) {
        SagaInstance saga = SagaInstance.builder()
            .sagaId(sagaId)
            .sagaType(sagaType)
            .status(SagaStatus.STARTED)
            .build();

        sagaRepository.save(saga);
    }

    @Transactional
    public void markStepSent(
        String sagaId,
        int stepOrder,
        String stepName,
        String commandType,
        Object payload,
        SagaStatus sagaStatus
    ) {
        SagaInstance saga = sagaRepository.getReferenceById(sagaId);

        saga.setStatus(sagaStatus);
        saga.setCurrentStep(stepName);

        SagaStep step = SagaStep.builder()
            .saga(saga)
            .stepOrder(stepOrder)
            .stepName(stepName)
            .status(SagaStepStatus.SENT)
            .commandType(commandType)
            .payload(toMap(payload))
            .build();

        sagaRepository.save(saga);
        stepRepository.save(step);
    }

    @Transactional
    public void markStepCompleted(
        String sagaId,
        String stepName,
        String eventType,
        Object responsePayload
    ) {
        SagaStep step = stepRepository
            .findBySagaSagaIdAndStepNameAndStatus(sagaId, stepName, SagaStepStatus.SENT)
            .orElseThrow(() -> new IllegalStateException(
                "Step enviado não encontrado para sagaId=" + sagaId + ", stepName=" + stepName
            ));

        step.setStatus(SagaStepStatus.COMPLETED);
        step.setEventType(eventType);
        step.setResponsePayload(toMap(responsePayload));
        step.setCompletedAt(LocalDateTime.now());

        stepRepository.save(step);
    }

    @Transactional
    public void completeSaga(String sagaId) {
        SagaInstance saga = sagaRepository.getReferenceById(sagaId);
        saga.setStatus(SagaStatus.COMPLETED);
        saga.setCurrentStep(null);
        saga.setFinishedAt(LocalDateTime.now());
        sagaRepository.save(saga);
    }

    @Transactional
    public void failStep(
        String sagaId,
        String stepName,
        String errorMessage
    ) {
        stepRepository
            .findBySagaSagaIdAndStepNameAndStatus(sagaId, stepName, SagaStepStatus.SENT)
            .ifPresent(step -> {
                step.setStatus(SagaStepStatus.FAILED);
                step.setErrorMessage(errorMessage);
                step.setCompletedAt(LocalDateTime.now());
                stepRepository.save(step);
            });
    }

    @Transactional
    public void failSaga(String sagaId, String errorMessage) {
        SagaInstance saga = sagaRepository.getReferenceById(sagaId);
        saga.setStatus(SagaStatus.FAILED);
        saga.setErrorMessage(errorMessage);
        saga.setFinishedAt(LocalDateTime.now());
        sagaRepository.save(saga);
    }

    @Transactional
    public void requireCompensation(String sagaId, String errorMessage) {
        SagaInstance saga = sagaRepository.getReferenceById(sagaId);
        saga.setStatus(SagaStatus.COMPENSATION_REQUIRED);
        saga.setErrorMessage(errorMessage);
        sagaRepository.save(saga);
    }


    private Map<String, Object> toMap(Object payload) {
        if (payload == null) {
            return null;
        }

        return objectMapper.convertValue(
            payload,
            new TypeReference<Map<String, Object>>() {}
        );
    }
}
