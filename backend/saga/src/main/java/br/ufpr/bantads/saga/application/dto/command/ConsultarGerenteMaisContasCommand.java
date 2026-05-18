package br.ufpr.bantads.saga.application.dto.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultarGerenteMaisContasCommand {

    private String sagaId;
}