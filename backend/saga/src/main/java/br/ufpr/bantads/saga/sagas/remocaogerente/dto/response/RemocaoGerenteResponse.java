package br.ufpr.bantads.saga.sagas.remocaogerente.dto.response;

import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ContasReatribuidasEvent;

public record RemocaoGerenteResponse(
    String cpf,
    String gerenteDestinoCpf,
    Long contasReatribuidas
) {

    public static RemocaoGerenteResponse fromContasReatribuidas(String cpfRemovido, ContasReatribuidasEvent event) {
        return new RemocaoGerenteResponse(
            cpfRemovido,
            event.getGerenteDestinoCpf(),
            event.getContasReatribuidas()
        );
    }
}