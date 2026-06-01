package com.ufpr.bantads.conta.application.dto.shared;

public record GerenteCandidato(
    String cpf,
    String nome,
    String email,
    String tipo
) {}
