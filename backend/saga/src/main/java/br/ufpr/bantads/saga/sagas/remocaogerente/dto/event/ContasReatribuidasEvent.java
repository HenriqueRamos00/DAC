package br.ufpr.bantads.saga.sagas.remocaogerente.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContasReatribuidasEvent {

    private String sagaId;
    private String gerenteDestinoCpf;
    private Long contasReatribuidas;
}