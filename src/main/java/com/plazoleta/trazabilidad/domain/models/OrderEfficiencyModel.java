package com.plazoleta.trazabilidad.domain.models;

import java.time.Duration;

public class OrderEfficiencyModel {
    private final Long orderId;
    private final Long employeeId;
    private final Duration timeTaken;

    public OrderEfficiencyModel(Long orderId, Long employeeId, Duration timeTaken) {
        this.orderId    = orderId;
        this.employeeId = employeeId;
        this.timeTaken  = timeTaken;
    }

    public Long getOrderId()    { return orderId; }
    public Long getEmployeeId() { return employeeId; }
    public Duration getTimeTaken() { return timeTaken; }
}
