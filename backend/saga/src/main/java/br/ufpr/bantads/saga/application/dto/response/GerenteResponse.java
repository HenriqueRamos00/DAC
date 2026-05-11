package br.ufpr.bantads.saga.application.dto.response;

public record GerenteResponse(
    String cpf,
    String nome,
    String email,
    String tipo
) {
}
