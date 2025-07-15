package com.plazoleta.trazabilidad.domain.exceptions;

public class IncompleteTraceabilityException extends RuntimeException {
    public IncompleteTraceabilityException(String message) {
        super(message);
    }
}
