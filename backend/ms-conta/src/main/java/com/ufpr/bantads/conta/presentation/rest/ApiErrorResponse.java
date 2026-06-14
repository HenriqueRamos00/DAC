package com.ufpr.bantads.conta.presentation.rest;

public record ApiErrorResponse(
    int status,
    String error,
    String message
) {
}
