package br.ufpr.bantads.saga.shared.dto.response;

import java.text.Normalizer;

import org.springframework.http.HttpStatus;

public final class SagaErrorMapper {

    private SagaErrorMapper() {}

    public static HttpStatus toHttpStatus(SagaErrorResponse error) {
        if (error == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        if (error.errorCode() != null) {
            return error.errorCode().httpStatus();
        }
        return inferFromMotivo(error.motivo()).httpStatus();
    }

    public static SagaErrorCode inferFromMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            return SagaErrorCode.INTERNAL;
        }
        String normalized = normalize(motivo);

        if (containsAny(normalized,
            "nao encontrado", "not found", "inexistente",
            "gerente_nao_encontrado")) {
            return SagaErrorCode.NOT_FOUND;
        }

        if (containsAny(normalized,
            "ultimo_gerente", "ja aprovado", "ja existe", "duplicado",
            "ja cadastrado", "cpf_duplicado", "ja esta")) {
            return SagaErrorCode.CONFLICT;
        }

        if (containsAny(normalized,
            "nao concluida", "timeout", "tempo esgotado")) {
            return SagaErrorCode.TIMEOUT;
        }

        return SagaErrorCode.INTERNAL;
    }

    private static boolean containsAny(String normalized, String... needles) {
        for (String needle : needles) {
            if (normalized.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        String stripped = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return stripped.toLowerCase();
    }
}