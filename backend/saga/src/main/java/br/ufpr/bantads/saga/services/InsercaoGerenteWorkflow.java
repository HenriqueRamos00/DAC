package br.ufpr.bantads.saga.services;

import java.util.List;

public class InsercaoGerenteWorkflow implements Workflow {

    private final List<WorkflowStep> steps;

    public InsercaoGerenteWorkflow(List<WorkflowStep> steps) {
        this.steps = steps;
    }

    @Override
    public List<WorkflowStep> getSteps() {
        return steps;
    }
}
