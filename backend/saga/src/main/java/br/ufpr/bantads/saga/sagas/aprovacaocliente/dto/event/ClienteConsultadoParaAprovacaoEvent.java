package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.shared.ClienteAprovacaoDados;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClienteConsultadoParaAprovacaoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email,
    BigDecimal salario
) {
    public ClienteAprovacaoDados toClienteAprovacaoDados() {
        return new ClienteAprovacaoDados(cpf, nome, email, salario);
    }
}
