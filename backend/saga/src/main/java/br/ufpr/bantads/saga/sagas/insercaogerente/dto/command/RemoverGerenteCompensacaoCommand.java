package br.ufpr.bantads.saga.sagas.insercaogerente.dto.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoverGerenteCompensacaoCommand {

    private String sagaId;
    private String cpf;
}