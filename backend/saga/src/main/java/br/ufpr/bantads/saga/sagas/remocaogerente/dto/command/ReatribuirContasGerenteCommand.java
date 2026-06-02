package br.ufpr.bantads.saga.sagas.remocaogerente.dto.command;

import java.util.List;

import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.GerentesAtivosListadosEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReatribuirContasGerenteCommand {

    private String sagaId;
    private String cpfOrigem;
    private List<String> candidatosDestino;

    public static ReatribuirContasGerenteCommand fromGerentesAtivos(String cpfOrigem, GerentesAtivosListadosEvent event) {
        return new ReatribuirContasGerenteCommand(
            event.getSagaId(),
            cpfOrigem,
            event.getCpfs()
        );
    }
}