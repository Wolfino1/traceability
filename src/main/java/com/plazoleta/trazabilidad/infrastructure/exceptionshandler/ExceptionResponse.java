package com.plazoleta.trazabilidad.infrastructure.exceptionshandler;

import java.time.LocalDateTime;

public record ExceptionResponse (String message, LocalDateTime timeStamp) {
}
