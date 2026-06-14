package br.ufpr.bantads.saga.sagas.remocaogerente.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReatribuicaoContasRevertidaCompensacaoEvent {

    private String sagaId;
    private Long contasRevertidas;
}