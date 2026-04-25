package com.ufpr.bantads.cliente.presentation.advice;

import com.ufpr.bantads.cliente.domain.exception.ClienteNaoPendenteException;
import com.ufpr.bantads.cliente.domain.exception.EmailJaCadastradoException;
import com.ufpr.bantads.cliente.domain.exception.TelefoneJaCadastradoException;
import com.ufpr.bantads.cliente.domain.exception.CpfJaCadastradoException;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<ApiErrorResponse> handleCpfJaCadastrado(CpfJaCadastradoException ex) {
        HttpStatus status = HttpStatus.CONFLICT;

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailJaCadastrado(EmailJaCadastradoException ex) {
        HttpStatus status = HttpStatus.CONFLICT;

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(TelefoneJaCadastradoException.class)
    public ResponseEntity<ApiErrorResponse> handleTelefoneJaCadastrado(TelefoneJaCadastradoException ex) {
        HttpStatus status = HttpStatus.CONFLICT;

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleClienteNaoEncontrado(ClienteNaoEncontradoException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ClienteNaoPendenteException.class)
    public ResponseEntity<ApiErrorResponse> handleClienteNaoPendente(ClienteNaoPendenteException ex) {
        HttpStatus status = HttpStatus.CONFLICT;

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = "Violacao de integridade de dados";

        String exceptionMessage = ex.getMostSpecificCause() != null
            ? ex.getMostSpecificCause().getMessage()
            : ex.getMessage();

        if (exceptionMessage != null) {
            if (exceptionMessage.contains("cliente_email_key")) {
                message = "Ja existe cliente cadastrado com este email";
            } else if (exceptionMessage.contains("cliente_telefone_key")) {
                message = "Ja existe cliente cadastrado com este telefone";
            } else if (exceptionMessage.contains("cliente_cpf_key")) {
                message = "Ja existe cliente cadastrado com este CPF";
            }
        }

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            message
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("Dados invalidos");

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            message
        );

        return ResponseEntity.status(status).body(response);
    }
}
