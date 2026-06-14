package br.ufpr.bantads.saga.shared.dto.response;

/**
 * Marker para o retorno de qualquer orchestrator: SagaErrorResponse ou um response específico
 * da saga ({@code GerenteResponse}, {@code AlterarPerfilSagaResponse}, etc).
 */
public interface SagaResult {}