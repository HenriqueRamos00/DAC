package br.ufpr.bantads.saga.sagas.insercaogerente.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsercaoGerenteFalhouEvent {

    private String sagaId;
    private String motivo;
}