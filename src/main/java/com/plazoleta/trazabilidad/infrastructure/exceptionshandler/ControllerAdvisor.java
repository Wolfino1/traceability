package com.plazoleta.trazabilidad.infrastructure.exceptionshandler;

import com.plazoleta.trazabilidad.domain.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice

public class ControllerAdvisor {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidPaginationException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidPaginationException(InvalidPaginationException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(WrongArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleWrongArgumentException(WrongArgumentException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidOrderException(InvalidOrderException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(IncompleteTraceabilityException.class)
    public ResponseEntity<ExceptionResponse> handleIncompleteTraceabilityException(IncompleteTraceabilityException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage(),
                LocalDateTime.now()));
    }
}
