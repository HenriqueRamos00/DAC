package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event;

public record GerenteParaNovaContaSelecionadoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email
) {}
