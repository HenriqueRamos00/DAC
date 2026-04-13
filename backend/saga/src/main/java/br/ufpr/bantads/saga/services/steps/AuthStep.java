package br.ufpr.bantads.saga.services.steps;

import br.ufpr.bantads.saga.services.WorkflowStep;
import br.ufpr.bantads.saga.services.WorkflowStepStatus;
import org.springframework.web.client.RestClient;

public class AuthStep implements WorkflowStep {

    private final RestClient restClient;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public AuthStep(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public boolean process() {
        // TODO: POST para MS Auth (criar credenciais)
        return false;
    }

    @Override
    public boolean revert() {
        // TODO: DELETE/compensação no MS Auth
        return false;
    }
}
