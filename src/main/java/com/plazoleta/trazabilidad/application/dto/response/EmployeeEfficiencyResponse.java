package com.plazoleta.trazabilidad.application.dto.response;

import java.time.Duration;

public record EmployeeEfficiencyResponse(
        Long employeeId,
        Duration averageTime
) {}
