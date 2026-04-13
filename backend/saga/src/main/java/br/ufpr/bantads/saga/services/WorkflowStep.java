package br.ufpr.bantads.saga.services;

public interface WorkflowStep {

    WorkflowStepStatus getStatus();

    boolean process();

    boolean revert();
}
