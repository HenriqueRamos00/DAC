package br.ufpr.bantads.saga.sagas.remocaogerente.dto.response;

import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ContasReatribuidasEvent;
import br.ufpr.bantads.saga.shared.dto.response.SagaResult;

public record RemocaoGerenteResponse(
    String cpf,
    String gerenteDestinoCpf,
    Long contasReatribuidas
) implements SagaResult {

    public static RemocaoGerenteResponse fromContasReatribuidas(String cpfRemovido, ContasReatribuidasEvent event) {
        return new RemocaoGerenteResponse(
            cpfRemovido,
            event.getGerenteDestinoCpf(),
            event.getContasReatribuidas()
        );
    }
}