package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.response;
public record EnderecoResponse(
    String cep,
    String logradouro,
    String numero,
    String complemento,
    String cidade,
    String estado
) {}
