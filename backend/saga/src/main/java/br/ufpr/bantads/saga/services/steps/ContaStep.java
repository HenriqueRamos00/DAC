package br.ufpr.bantads.saga.services.steps;

import br.ufpr.bantads.saga.services.WorkflowStep;
import br.ufpr.bantads.saga.services.WorkflowStepStatus;
import org.springframework.web.client.RestClient;

public class ContaStep implements WorkflowStep {

    private final RestClient restClient;
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING;

    public ContaStep(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return status;
    }

    @Override
    public boolean process() {
        // TODO: POST para MS Conta (criar conta, atribuir gerente)
        return false;
    }

    @Override
    public boolean revert() {
        // TODO: DELETE/compensação no MS Conta
        return false;
    }
}
