package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.shared;

public record GerenteCandidato(
    String cpf,
    String nome,
    String email,
    String tipo
) {}
