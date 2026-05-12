package com.ufpr.bantads.ms_gerente.presentation.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ufpr.bantads.ms_gerente.domain.exception.FiltroInvalidoException;
import com.ufpr.bantads.ms_gerente.domain.exception.GerenteNaoEncontradoException;
import com.ufpr.bantads.ms_gerente.domain.exception.UsuarioNaoAutenticadoException;
import com.ufpr.bantads.ms_gerente.domain.exception.UsuarioSemPermissaoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FiltroInvalidoException.class)
    public ResponseEntity<ApiErrorResponse> handleFiltroInvalido(FiltroInvalidoException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UsuarioNaoAutenticadoException.class)
    public ResponseEntity<ApiErrorResponse> handleUsuarioNaoAutenticado(UsuarioNaoAutenticadoException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UsuarioSemPermissaoException.class)
    public ResponseEntity<ApiErrorResponse> handleUsuarioSemPermissao(UsuarioSemPermissaoException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(GerenteNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleGerenteNaoEncontrado(GerenteNaoEncontradoException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message) {
        ApiErrorResponse body = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            message
        );
        return ResponseEntity.status(status).body(body);
    }
}
