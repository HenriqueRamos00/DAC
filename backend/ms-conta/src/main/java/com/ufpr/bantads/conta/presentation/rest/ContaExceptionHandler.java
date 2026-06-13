package com.ufpr.bantads.conta.presentation.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ufpr.bantads.conta.domain.exception.ContaJaExisteException;
import com.ufpr.bantads.conta.domain.exception.ContaNaoEncontradaException;
import com.ufpr.bantads.conta.domain.exception.NumeroContaIndisponivelException;
import com.ufpr.bantads.conta.domain.exception.RegraNegocioException;
import com.ufpr.bantads.conta.domain.exception.RequisicaoInvalidaException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ContaExceptionHandler {

    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ResponseEntity<ApiErrorResponse> handleRequisicaoInvalida(RequisicaoInvalidaException ex) {
        return erro(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> handleContaNaoEncontrada(ContaNaoEncontradaException ex) {
        return erro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
        ContaJaExisteException.class,
        NumeroContaIndisponivelException.class,
        RegraNegocioException.class
    })
    public ResponseEntity<ApiErrorResponse> handleConflito(RuntimeException ex) {
        return erro(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleErroInesperado(Exception ex) {
        log.error("Erro inesperado ao processar requisição", ex);
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar requisição");
    }

    private ResponseEntity<ApiErrorResponse> erro(HttpStatus status, String message) {
        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            message
        );
        return ResponseEntity.status(status).body(response);
    }
}
