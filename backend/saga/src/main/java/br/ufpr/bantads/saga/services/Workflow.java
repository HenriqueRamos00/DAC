package br.ufpr.bantads.saga.services;

import java.util.List;

public interface Workflow {

    List<WorkflowStep> getSteps();
}
