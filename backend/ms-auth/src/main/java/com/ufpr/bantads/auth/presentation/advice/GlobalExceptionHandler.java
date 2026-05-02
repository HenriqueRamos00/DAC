package com.ufpr.bantads.auth.presentation.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ufpr.bantads.auth.domain.exception.UsuarioSenhaIncorretosException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioSenhaIncorretosException.class)
    public ResponseEntity<ApiErrorResponse> handleUsuarioSenhaIncorretos(
        UsuarioSenhaIncorretosException ex) {
            
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ApiErrorResponse response = new ApiErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage()
        ); 

        return ResponseEntity.status(status).body(response);
    }

}
