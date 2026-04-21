package com.ufpr.bantads.cliente.presentation.advice;

public record ApiErrorResponse(
    int status,
    String error,
    String message
) {
}
