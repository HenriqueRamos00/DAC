package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.response;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ClienteAprovadoEvent;
import br.ufpr.bantads.saga.shared.dto.response.SagaResult;

public record ClienteAprovadoSagaResponse(
    String cpf,
    String email,
    String status
) implements SagaResult {
    public static ClienteAprovadoSagaResponse fromEvent(ClienteAprovadoEvent event) {
        return new ClienteAprovadoSagaResponse(
            event.cpf(),
            event.email(),
            event.status()
        );
    }
}
