package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.response;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ClienteAprovadoEvent;

public record ClienteAprovadoSagaResponse(
    String cpf,
    String email,
    String status
) {
    public static ClienteAprovadoSagaResponse fromEvent(ClienteAprovadoEvent event) {
        return new ClienteAprovadoSagaResponse(
            event.cpf(),
            event.email(),
            event.status()
        );
    }
}
