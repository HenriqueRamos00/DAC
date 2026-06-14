package br.ufpr.bantads.saga.sagas.remocaogerente.dto.command;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReverterReatribuicaoContasCompensacaoCommand {

    private String sagaId;
    private String gerenteOriginalCpf;
    private String gerenteDestinoCpf;
    private List<String> numerosContas;
}