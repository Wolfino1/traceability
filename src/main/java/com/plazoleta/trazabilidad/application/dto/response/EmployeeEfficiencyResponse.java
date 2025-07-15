package com.plazoleta.trazabilidad.application.dto.response;

public record EmployeeEfficiencyResponse(
        Long employeeId,
        String employeeEmail,
        double averageTime
) {}
