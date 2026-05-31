package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command;

import java.math.BigDecimal;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.GerenteParaNovaContaSelecionadoEvent;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.shared.ClienteAprovacaoDados;

public record CriarContaCommand(
    String sagaId,
    String clienteCpf,
    String clienteNome,
    BigDecimal salario,
    String gerenteCpf,
    String gerenteNome,
    String gerenteEmail
) {
    public static CriarContaCommand from(
        String sagaId,
        ClienteAprovacaoDados cliente,
        GerenteParaNovaContaSelecionadoEvent gerente
    ) {
        return new CriarContaCommand(
            sagaId,
            cliente.cpf(),
            cliente.nome(),
            cliente.salario(),
            gerente.cpf(),
            gerente.nome(),
            gerente.email()
        );
    }
}
