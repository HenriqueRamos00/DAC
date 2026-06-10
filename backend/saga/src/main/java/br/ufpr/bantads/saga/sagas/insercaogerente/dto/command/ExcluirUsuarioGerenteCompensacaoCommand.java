package br.ufpr.bantads.saga.sagas.insercaogerente.dto.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcluirUsuarioGerenteCompensacaoCommand {

    private String sagaId;
    private String cpf;
    private String email;
}