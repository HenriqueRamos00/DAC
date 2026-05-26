package br.ufpr.bantads.saga.sagas.insercaogerente.dto.command;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteInseridoEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtribuirGerenteContaCommand {

    private String sagaId;
    private String gerenteOriginalCpf;
    private String novoGerenteCpf;

    public static AtribuirGerenteContaCommand fromGerenteInserido(
        String gerenteOriginalCpf,
        GerenteInseridoEvent event
    ) {
        return new AtribuirGerenteContaCommand(
            event.getSagaId(),
            gerenteOriginalCpf,
            event.getCpf()
        );
    }
}