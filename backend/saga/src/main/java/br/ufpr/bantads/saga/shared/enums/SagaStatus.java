package br.ufpr.bantads.saga.shared.enums;

public enum SagaStatus {
    STARTED,
    EXECUTING,
    COMPLETED,
    FAILED,
    COMPENSATION_REQUIRED,
    COMPENSATED
}
