package com.ufpr.bantads.ms_gerente.presentation.advice;

public record ApiErrorResponse(
    int status,
    String error,
    String message
) {
}
