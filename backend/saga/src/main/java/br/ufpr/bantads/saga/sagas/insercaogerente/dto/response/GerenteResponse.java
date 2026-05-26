package br.ufpr.bantads.saga.sagas.insercaogerente.dto.response;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteInseridoEvent;

public record GerenteResponse(
    String cpf,
    String nome,
    String email,
    String tipo
) {

    public static GerenteResponse fromEvent(GerenteInseridoEvent event) {
        return new GerenteResponse(
            event.getCpf(),
            event.getNome(),
            event.getEmail(),
            event.getTipo()
        );
    }
}