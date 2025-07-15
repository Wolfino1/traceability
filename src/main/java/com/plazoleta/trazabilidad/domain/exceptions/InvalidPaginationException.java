package com.plazoleta.trazabilidad.domain.exceptions;

public class InvalidPaginationException extends RuntimeException {
    public InvalidPaginationException(String message) {
        super(message);
    }
}
