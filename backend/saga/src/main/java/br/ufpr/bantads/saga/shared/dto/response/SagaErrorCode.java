package br.ufpr.bantads.saga.shared.dto.response;

import org.springframework.http.HttpStatus;

public enum SagaErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND),
    CONFLICT(HttpStatus.CONFLICT),
    INVALID_INPUT(HttpStatus.BAD_REQUEST),
    UNPROCESSABLE(HttpStatus.UNPROCESSABLE_CONTENT),
    TIMEOUT(HttpStatus.GATEWAY_TIMEOUT),
    INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus httpStatus;

    SagaErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}