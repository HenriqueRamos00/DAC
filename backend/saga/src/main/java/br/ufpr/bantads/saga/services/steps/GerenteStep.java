package br.ufpr.bantads.saga.services.steps;

import br.ufpr.bantads.saga.services.WorkflowStep;
import br.ufpr.bantads.saga.services.WorkflowStepStatus;
import org.springframework.web.client.RestClient;

public class GerenteStep implements WorkflowStep {

    private final RestClient restClient;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public GerenteStep(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public boolean process() {
        // TODO: chamada ao MS Gerente
        return false;
    }

    @Override
    public boolean revert() {
        // TODO: compensação no MS Gerente
        return false;
    }
}
