package com.plazoleta.trazabilidad.application.dto.request;

import com.plazoleta.trazabilidad.domain.models.OrderStatus;

import java.time.LocalDateTime;

public record CreateTraceabilityRequest(Long orderId,
                                        Long clientId,
                                        String clientEmail,
                                        LocalDateTime date,
                                        OrderStatus previousState,
                                        OrderStatus newState,
                                        Long employeeId,
                                        String employeeEmail) {
}
