package br.ufpr.bantads.saga.sagas.remocaogerente.dto.command;

import java.util.List;

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
}