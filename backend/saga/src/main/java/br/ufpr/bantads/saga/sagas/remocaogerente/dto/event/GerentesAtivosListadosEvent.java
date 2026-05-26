package br.ufpr.bantads.saga.sagas.remocaogerente.dto.event;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerentesAtivosListadosEvent {

    private String sagaId;
    private List<String> cpfs;
}