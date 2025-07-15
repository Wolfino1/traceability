package com.plazoleta.trazabilidad.domain.models;

import java.time.Duration;

public class EmployeeEfficiencyModel {


    Long employeeId;
    Duration averageTime;

    public EmployeeEfficiencyModel(Long employeeId, Duration averageTime) {
        this.employeeId = employeeId;
        this.averageTime = averageTime;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Duration getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(Duration averageTime) {
        this.averageTime = averageTime;
    }
}