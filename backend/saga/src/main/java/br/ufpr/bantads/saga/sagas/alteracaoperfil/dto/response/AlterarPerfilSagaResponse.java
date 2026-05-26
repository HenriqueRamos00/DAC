package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.response;

import java.math.BigDecimal;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ContaLimiteAlteradoEvent;

public record AlterarPerfilSagaResponse(
    String cpf,
    String nome,
    String email,
    String telefone,
    BigDecimal salario,
    EnderecoResponse endereco,
    String conta,
    BigDecimal saldo,
    BigDecimal limite,
    String gerente
) {

    public static AlterarPerfilSagaResponse fromEvent(
        ClientePerfilAlteradoEvent clienteEvent,
        ContaLimiteAlteradoEvent contaEvent
    ) {
        return new AlterarPerfilSagaResponse(
            clienteEvent.getCpf(),
            clienteEvent.getNome(),
            clienteEvent.getEmail(),
            clienteEvent.getTelefone(),
            clienteEvent.getSalario(),
            new EnderecoResponse(
                clienteEvent.getCep(),
                clienteEvent.getLogradouro(),
                clienteEvent.getNumero(),
                clienteEvent.getComplemento(),
                clienteEvent.getCidade(),
                clienteEvent.getEstado()
            ),
            contaEvent.getNumeroConta(),
            contaEvent.getSaldo(),
            contaEvent.getLimite(),
            contaEvent.getGerenteCpf()
        );
    }

}
