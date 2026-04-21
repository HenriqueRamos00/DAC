package com.ufpr.bantads.conta.application.dto.request;

public record TransferenciaRequest(String numeroContaOrigem, String numeroContaDestino, Double valor) {
    
}
