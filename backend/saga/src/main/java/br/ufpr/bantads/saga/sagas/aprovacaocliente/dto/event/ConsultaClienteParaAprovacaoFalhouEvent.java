package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConsultaClienteParaAprovacaoFalhouEvent(
    String sagaId,
    String cpf,
    String motivo
) {}
