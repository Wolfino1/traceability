package com.plazoleta.trazabilidad.application.dto.response;

import com.plazoleta.trazabilidad.domain.models.OrderStatus;

import java.time.LocalDateTime;

public record TraceabilityClientResponse (Long orderId, LocalDateTime date,
                                         OrderStatus previousState,
                                         OrderStatus newState){
}
