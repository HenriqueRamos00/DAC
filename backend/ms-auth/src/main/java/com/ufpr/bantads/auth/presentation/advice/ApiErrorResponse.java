package com.ufpr.bantads.auth.presentation.advice;

public record ApiErrorResponse(
    int status,
    String error,
    String message
) {
}