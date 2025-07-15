package com.plazoleta.trazabilidad.domain.exceptions;

public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String message) {
        super(message);
    }
}
