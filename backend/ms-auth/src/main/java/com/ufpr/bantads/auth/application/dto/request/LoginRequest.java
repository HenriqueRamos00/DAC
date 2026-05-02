package com.ufpr.bantads.auth.application.dto.request;

public record LoginRequest(
    String login,
    String senha
) {}
