package br.ufpr.bantads.saga.sagas.remocaogerente.dto.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoverGerenteCommand {

    private String sagaId;
    private String cpf;
}