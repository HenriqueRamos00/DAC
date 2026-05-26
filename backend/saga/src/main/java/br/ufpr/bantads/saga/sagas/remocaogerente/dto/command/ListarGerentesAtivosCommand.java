package br.ufpr.bantads.saga.sagas.remocaogerente.dto.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListarGerentesAtivosCommand {

    private String sagaId;
    private String cpfRemovendo;
}