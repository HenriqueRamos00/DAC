package br.ufpr.bantads.saga.services.steps;

import br.ufpr.bantads.saga.services.WorkflowStep;
import br.ufpr.bantads.saga.services.WorkflowStepStatus;
import org.springframework.web.client.RestClient;

public class ClienteStep implements WorkflowStep {

    private final RestClient restClient;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public ClienteStep(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public boolean process() {
        // TODO: POST para MS Cliente
        return false;
    }

    @Override
    public boolean revert() {
        // TODO: DELETE/compensação no MS Cliente
        return false;
    }
}
