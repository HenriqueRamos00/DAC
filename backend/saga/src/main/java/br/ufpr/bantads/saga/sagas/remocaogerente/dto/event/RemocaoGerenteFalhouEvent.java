package br.ufpr.bantads.saga.sagas.remocaogerente.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemocaoGerenteFalhouEvent {

    private String sagaId;
    private String motivo;
}