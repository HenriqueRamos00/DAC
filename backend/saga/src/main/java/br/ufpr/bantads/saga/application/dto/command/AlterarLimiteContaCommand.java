package br.ufpr.bantads.saga.application.dto.command;

import java.math.BigDecimal;

import br.ufpr.bantads.saga.application.dto.event.ClientePerfilAlteradoEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlterarLimiteContaCommand {

    private String sagaId;
    private String cpf;
    private String clienteNome;
    private BigDecimal salario;

    public static AlterarLimiteContaCommand fromEvent(ClientePerfilAlteradoEvent event) {
        return new AlterarLimiteContaCommand(
            event.getSagaId(),
            event.getCpf(),
            event.getNome(),
            event.getSalario()
        );
    }
}
