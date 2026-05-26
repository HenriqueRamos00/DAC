package br.ufpr.bantads.saga.sagas.remocaogerente.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReatribuicaoContasFalhouEvent {

    private String sagaId;
    private String motivo;
}