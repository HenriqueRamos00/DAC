package br.ufpr.bantads.saga.services;

import java.util.List;

public class RemocaoGerenteWorkflow implements Workflow {

    private final List<WorkflowStep> steps;

    public RemocaoGerenteWorkflow(List<WorkflowStep> steps) {
        this.steps = steps;
    }

    @Override
    public List<WorkflowStep> getSteps() {
        return steps;
    }
}
