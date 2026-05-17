package br.ufpr.bantads.saga.application.dto.response;

public record EnderecoResponse(
    String cep,
    String logradouro,
    String numero,
    String complemento,
    String cidade,
    String estado
) {}
