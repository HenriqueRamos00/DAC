package br.ufpr.bantads.saga.services.exceptions;

public class WorkflowException extends RuntimeException {

    public WorkflowException(String message) {
        super(message);
    }
}
