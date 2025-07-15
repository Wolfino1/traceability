package com.plazoleta.trazabilidad.application.dto.response;

import java.time.Duration;

public record OrderEfficiencyResponse(
        Long orderId,
        Long employeeId,
        Duration timeTaken) {}
